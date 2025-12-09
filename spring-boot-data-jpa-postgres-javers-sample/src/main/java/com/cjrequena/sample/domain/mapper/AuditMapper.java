package com.cjrequena.sample.domain.mapper;

import com.cjrequena.sample.controller.dto.audit.AuditSnapshotDTO;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditMapper {

    // ---------------------------
    // Snapshot → DTO
    // ---------------------------
    @Mapping(target = "commitId", expression = "java(snapshot.getCommitId().toString())")
    @Mapping(target = "commitDate", expression = "java(snapshot.getCommitMetadata().getCommitDate())")
    @Mapping(target = "author", expression = "java(snapshot.getCommitMetadata().getAuthor())")
    @Mapping(target = "properties", expression = "java(snapshot.getCommitMetadata().getProperties())")
    @Mapping(target = "state", expression = "java(snapshot.getState())")
    @Mapping(target = "changedProperties", expression = "java(snapshot.getChanged())")
    AuditSnapshotDTO toSnapshotDTO(CdoSnapshot snapshot);


    // ---------------------------
    // Snapshot → DTO
    // ---------------------------
//    @Mapping(target = "entityId", expression = "java(snapshot.getGlobalId().value())")
//    @Mapping(target = "commitDate", expression = "java(snapshot.getCommitMetadata().getCommitDate())")
//    @Mapping(target = "author", expression = "java(snapshot.getCommitMetadata().getAuthor())")
//    @Mapping(target = "ruleId", expression = "java(snapshot.getCommitMetadata().getProperties().get(\"ruleId\"))")
//    @Mapping(target = "action", expression = "java(snapshot.getCommitMetadata().getProperties().get(\"action\"))")
//    @Mapping(target = "justification", expression = "java(snapshot.getCommitMetadata().getProperties().get(\"justification\"))")
//    @Mapping(target = "performedAt", expression = "java(snapshot.getCommitMetadata().getProperties().get(\"performedAt\"))")
//    @Mapping(target = "state", expression = "java(snapshot.getState())")
//    AuditRuleDTO toRuleDTO(CdoSnapshot snapshot);
}
