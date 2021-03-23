package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Issue;
import es.upm.miw.betca_tpv_core.domain.rest.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IssueService {

    private GitHubService gitHubService;

    @Autowired
    public IssueService(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    public Flux<Issue> findByTitleAndBodyAndLabelsAndStateAndMilestoneAndAssigneeNullSafe(
            String title, String body, String labels, String state, String milestone, String assignee
    ) {
        return this.gitHubService.search(labels, state, assignee)
                .filter(issue ->
                        (title.equals("") || issue.getTitle().toLowerCase().contains(title))
                        && (body.equals("") || issue.getBody().toLowerCase().contains(body))
                        && (milestone.equals("") || issue.getMilestone().getTitle().toLowerCase().contains(milestone))
                );
    }

    public Mono<Issue> read(Integer number) {
        return this.gitHubService.read(number);
    }

    public Mono<Issue> create(Issue issue) {
        return null; // TODO implement
    }
}
