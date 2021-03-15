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

    public Flux<Issue> findByTitleAndBodyAndLabelsAndStateAndMilestoneAndAssigneeNullSafe(String title, String body, String labels, String state, String milestone, String assignee) {
        return this.gitHubService.search(title, body, labels, state, milestone, assignee);
    }

    public Mono<Issue> read(Integer id) {
        return null; // TODO implement
    }

    public Mono<Issue> create(Issue issue) {
        return null; // TODO implement
    }
}
