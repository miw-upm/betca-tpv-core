package es.upm.miw.betca_tpv_core.domain.rest;

import es.upm.miw.betca_tpv_core.domain.model.Issue;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface GitHubService {

    Mono<Issue> create(Issue issue);

    Mono<Issue> read(Integer id);

    // TODO search
}
