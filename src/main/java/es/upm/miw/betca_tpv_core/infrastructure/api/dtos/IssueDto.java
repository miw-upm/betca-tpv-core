package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.Issue;
import es.upm.miw.betca_tpv_core.domain.model.IssueLabel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueDto {
    private Integer number;
    private String title;
    private String body;
    private String labels;
    private String state;
    private String assignee;
    private String milestone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created_at;

    public IssueDto(Issue issue) {
        this.number = issue.getNumber();
        this.title = issue.getTitle();
        this.body = issue.getBody();
        if (issue.getLabels() != null) {
            this.labels = Arrays.stream(issue.getLabels())
                    .map(IssueLabel::getName)
                    .collect(Collectors.joining(","));
        }
        this.state = issue.getState();
        if (issue.getAssignee() != null) {
            this.assignee = issue.getAssignee().getLogin();
        } else {
            this.assignee = null;
        }
        if (issue.getMilestone() != null) {
            this.milestone = issue.getMilestone().getTitle();
        } else {
            this.milestone = null;
        }
        this.created_at = issue.getCreated_at();
    }
}
