package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.controller.dto.audit.AuditShadowDTO;
import com.cjrequena.sample.controller.dto.audit.AuditSnapshotDTO;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.shadow.Shadow;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = "spring",
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class AuditMapper {

  @Mapping(target = "commitId", expression = "java(snapshot.getCommitId().toString())")
  @Mapping(target = "commitDate", expression = "java(snapshot.getCommitMetadata().getCommitDateInstant())")
  @Mapping(target = "author", expression = "java(snapshot.getCommitMetadata().getAuthor())")
  @Mapping(target = "action", expression = "java(snapshot.getCommitMetadata().getProperties().get(\"action\"))")
  @Mapping(target = "justification", expression = "java(snapshot.getCommitMetadata().getProperties().get(\"justification\"))")
  @Mapping(target = "performedAt", expression = "java(snapshot.getCommitMetadata().getProperties().get(\"performed_at\"))")
  //@Mapping(target = "properties", expression = "java(snapshot.getCommitMetadata().getProperties())")
  @Mapping(target = "state", expression = "java(snapshot.getState())")
  @Mapping(target = "changedProperties", expression = "java(snapshot.getChanged())")
  public abstract AuditSnapshotDTO toSnapshotDTO(CdoSnapshot snapshot);

  // Concrete, non-generic Shadow -> DTO for BookEntity
  public AuditShadowDTO<Object> toShadowDTO(Shadow<?> shadow) {
    if (shadow == null) return null;
    AuditShadowDTO<Object> dto = new AuditShadowDTO<>();
    dto.setEntity(shadow.get());
    dto.setCommitDate(shadow.getCommitMetadata().getCommitDateInstant());
    dto.setAuthor(shadow.getCommitMetadata().getAuthor());
    return dto;
  }
}
