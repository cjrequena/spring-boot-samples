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

import static com.cjrequena.sample.shared.common.util.Constant.VND_SAMPLE_SERVICE_V1;

@RestController
@RequestMapping(
  value = AuditController.ENDPOINT,
  headers = {AuditController.ACCEPT_VERSION}
)
@RequiredArgsConstructor
@Slf4j
public class AuditController {
  public static final String ENDPOINT = "/api/audits";
  public static final String ACCEPT_VERSION = "Accept-Version=" + VND_SAMPLE_SERVICE_V1;

  private final Javers javers;

  // -------------------------------------------------------------
  //  Book Snapshots
  // -------------------------------------------------------------
  @GetMapping("/books/{id}/snapshots")
  public ResponseEntity<List<CdoSnapshot>> getBookSnapshots(@PathVariable Long id) {

    List<CdoSnapshot> snapshots = javers.findSnapshots(
      QueryBuilder.byInstanceId(id, BookEntity.class).build()
    );

    return ResponseEntity.ok(snapshots);
  }

  // -------------------------------------------------------------
  //  Book Changes — Rich JSON
  // -------------------------------------------------------------
  @GetMapping("/books/{id}/changes")
  public ResponseEntity<List<Map<String, Object>>> getBookChanges(@PathVariable Long id) {

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
  @GetMapping("/books/{id}/history")
  public ResponseEntity<List<Shadow<BookEntity>>> getBookHistory(@PathVariable Long id) {

    List<Shadow<BookEntity>> shadows = javers.findShadows(
      QueryBuilder.byInstanceId(id, BookEntity.class).build()
    );

    return ResponseEntity.ok(shadows);
  }


  // -------------------------------------------------------------
  //  All Book Changes
  // -------------------------------------------------------------
  @GetMapping("/books/all")
  public ResponseEntity<List<CdoSnapshot>> getAllBookChanges(@RequestParam(defaultValue = "10") int limit) {
    List<CdoSnapshot> snapshots = javers.findSnapshots(
      QueryBuilder.byClass(BookEntity.class)
        .limit(limit)
        .build()
    );

    return ResponseEntity.ok(snapshots);
  }

}
