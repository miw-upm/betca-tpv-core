package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Issue {
    private Integer number;
    @NotBlank
    private String title;
    private String body;
    private IssueLabel[] labels;
    private String state;
    private IssueAssignee assignee;
    private IssueMilestone milestone;
    private LocalDateTime created_at;
}
