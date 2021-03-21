package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.Objects;

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
    @JsonProperty("collection")
    private IssueLabel[] labels;
    private String state;
    private IssueAssignee assignee;
    private IssueMilestone milestone;
    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created_at;

    public static Issue ofTitleBodyLabels(Issue issue) {
        return Issue.builder()
                .title(issue.getTitle())
                .body(issue.getBody())
                .labels(issue.getLabels())
                .build();
    }

    public void doDefault() {
        if (Objects.isNull(state)) {
            this.state = "open";
        }
    }
}
