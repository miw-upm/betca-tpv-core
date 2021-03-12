package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Issue;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class IssueService {

    public Flux<Issue> findByTitleAndBodyAndLabelsAndStateAndMilestoneAndAssigneeNullSafe(String title, String body, String labels, String state, String milestone, String assignee) {
        return Flux.empty(); // TODO implement
    }

    public Mono<Issue> read(Integer id) {
        return Mono.empty(); // TODO implement
    }

    public Mono<Issue> create(Issue issue) {
        return Mono.empty(); // TODO implement
    }
}
