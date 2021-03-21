package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import es.upm.miw.betca_tpv_core.domain.model.Issue;
import es.upm.miw.betca_tpv_core.domain.model.IssueLabel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IssueBasicDto {
    private Integer number;
    private String title;
    private String body;
    private String labels;

    public IssueBasicDto(Issue issue) {
        this.number = issue.getNumber();
        this.title = issue.getTitle();
        this.body = issue.getBody();
        if (issue.getLabels() != null) {
            this.labels = Arrays.stream(issue.getLabels())
                    .map(IssueLabel::getName)
                    .collect(Collectors.joining(","));
        }
    }
}
