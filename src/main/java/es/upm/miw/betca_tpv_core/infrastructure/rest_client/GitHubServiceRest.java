package es.upm.miw.betca_tpv_core.infrastructure.rest_client;

import es.upm.miw.betca_tpv_core.domain.model.Issue;
import es.upm.miw.betca_tpv_core.domain.rest.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service("gitHubClient")
public class GitHubServiceRest implements GitHubService {

    private String gitHubUri;
    private WebClient.Builder webClientBuilder;

    @Autowired
    public GitHubServiceRest(@Value("${miw.tpv.github}") String gitHubUri, WebClient.Builder webClientBuilder) {
        this.gitHubUri = gitHubUri;
        this.webClientBuilder = webClientBuilder;
    }

    // https://docs.github.com/en/rest/reference/issues#create-an-issue
    @Override
    public Mono<Issue> create(Issue issue) {
        return null; // TODO implement
    }

    // https://docs.github.com/en/rest/reference/issues#get-an-issue
    @Override
    public Mono<Issue> read(Integer id) {
        return null; // TODO implement
    }

    // https://docs.github.com/en/rest/reference/issues#list-repository-issues
}
