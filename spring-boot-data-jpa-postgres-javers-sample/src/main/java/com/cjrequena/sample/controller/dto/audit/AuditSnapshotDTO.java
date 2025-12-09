package com.cjrequena.sample.controller.dto.audit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditSnapshotDTO {

    private String commitId;
    private LocalDateTime commitDate;
    private String author;
    private Map<String, String> properties;
    private Object state;
    private List<String> changedProperties;
}
