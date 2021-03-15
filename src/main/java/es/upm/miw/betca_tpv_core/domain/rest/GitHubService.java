package es.upm.miw.betca_tpv_core.domain.rest;

import es.upm.miw.betca_tpv_core.domain.model.Issue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GitHubService {

    Mono<Issue> create(Issue issue);

    Mono<Issue> read(Integer id);

    Flux<Issue> search(String title, String body, String labels, String state, String milestone, String assignee);

}
