package com.cjrequena.sample.controller.rest;

import com.cjrequena.sample.persistence.entity.BookEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javers.core.Javers;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.jql.QueryBuilder;
import org.javers.shadow.Shadow;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
@Slf4j
public class AuditController {

  private final Javers javers;

  // -------------------------------------------------------------
  //  Book Snapshots
  // -------------------------------------------------------------
  @GetMapping("/book/{id}/snapshots")
  public ResponseEntity<List<CdoSnapshot>> getBookSnapshots(@PathVariable Long id) {
    log.info("GET /api/v1/audit/book/{}/snapshots", id);

    List<CdoSnapshot> snapshots = javers.findSnapshots(
      QueryBuilder.byInstanceId(id, BookEntity.class).build()
    );

    return ResponseEntity.ok(snapshots);
  }

  // -------------------------------------------------------------
  //  Book Changes — Rich JSON
  // -------------------------------------------------------------
  @GetMapping("/book/{id}/changes")
  public ResponseEntity<List<Map<String, Object>>> getBookChanges(@PathVariable Long id) {
    log.info("GET /api/v1/audit/book/{}/changes", id);

    List<CdoSnapshot> snapshots = javers.findSnapshots(
      QueryBuilder.byInstanceId(id, BookEntity.class).build()
    );

    List<Map<String, Object>> changes = snapshots.stream()
      .map(snapshot -> Map.of(
        "commitId", snapshot.getCommitId().toString(),
        "commitDate", snapshot.getCommitMetadata().getCommitDate(),
        "author", snapshot.getCommitMetadata().getAuthor(),
        "properties", snapshot.getCommitMetadata().getProperties(),
        "state", snapshot.getState(),
        "changedProperties", snapshot.getChanged()
      ))
      .toList();

    return ResponseEntity.ok(changes);
  }

  // -------------------------------------------------------------
  //  Book History (Shadows)
  // -------------------------------------------------------------
  @GetMapping("/book/{id}/history")
  public ResponseEntity<List<Shadow<BookEntity>>> getBookHistory(@PathVariable Long id) {
    log.info("GET /api/v1/audit/book/{}/history", id);

    List<Shadow<BookEntity>> shadows = javers.findShadows(
      QueryBuilder.byInstanceId(id, BookEntity.class).build()
    );

    return ResponseEntity.ok(shadows);
  }

  // -------------------------------------------------------------
  //  Changes by Author — FIXED for JaVers 7.x
  // -------------------------------------------------------------
  @GetMapping("/by-author/{author}")
  public ResponseEntity<List<CdoSnapshot>> getChangesByAuthor(@PathVariable String author) {
    log.info("GET /api/v1/audit/by-author/{}", author);

    List<CdoSnapshot> snapshots = javers.findSnapshots(
      QueryBuilder.anyDomainObject()
        .withCommitProperty("author", author)  // JaVers 7.x replacement for byAuthor()
        .build()
    );

    return ResponseEntity.ok(snapshots);
  }

  // -------------------------------------------------------------
  //  All Book Changes
  // -------------------------------------------------------------
  @GetMapping("/books/all")
  public ResponseEntity<List<CdoSnapshot>> getAllBookChanges(
    @RequestParam(defaultValue = "10") int limit
  ) {
    log.info("GET /api/v1/audit/books/all?limit={}", limit);

    List<CdoSnapshot> snapshots = javers.findSnapshots(
      QueryBuilder.byClass(BookEntity.class)
        .limit(limit)
        .build()
    );

    return ResponseEntity.ok(snapshots);
  }

  // -------------------------------------------------------------
  //  Changes By Custom Commit Property
  // -------------------------------------------------------------
  @GetMapping("/by-property")
  public ResponseEntity<List<CdoSnapshot>> getChangesByProperty(
    @RequestParam String propertyKey,
    @RequestParam String propertyValue
  ) {
    log.info("GET /api/v1/audit/by-property?propertyKey={}&propertyValue={}", propertyKey, propertyValue);

    List<CdoSnapshot> filtered = javers.findSnapshots(
      QueryBuilder.anyDomainObject()
        .withCommitProperty(propertyKey, propertyValue)
        .build()
    );

    return ResponseEntity.ok(filtered);
  }

  // -------------------------------------------------------------
  //  Changes by RuleId (Custom Business Audit Field)
  // -------------------------------------------------------------
  @GetMapping("/by-rule/{ruleId}")
  public ResponseEntity<List<Map<String, Object>>> getChangesByRuleId(@PathVariable String ruleId) {
    log.info("GET /api/v1/audit/by-rule/{}", ruleId);

    List<CdoSnapshot> snapshots = javers.findSnapshots(
      QueryBuilder.anyDomainObject()
        .withCommitProperty("ruleId", ruleId)
        .build()
    );

    List<Map<String, Object>> result = snapshots.stream()
      .map(snapshot -> Map.of(
        "bookId", snapshot.getGlobalId().value(),
        "commitDate", snapshot.getCommitMetadata().getCommitDate(),
        "author", snapshot.getCommitMetadata().getAuthor(),
        "ruleId", snapshot.getCommitMetadata().getProperties().get("ruleId"),
        "action", snapshot.getCommitMetadata().getProperties().get("action"),
        "justification", snapshot.getCommitMetadata().getProperties().get("justification"),
        "performedAt", snapshot.getCommitMetadata().getProperties().get("performedAt"),
        "state", snapshot.getState()
      ))
      .toList();

    return ResponseEntity.ok(result);
  }
}
