package es.upm.miw.betca_tpv_core.infrastructure.rest_client;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadGatewayException;
import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Issue;
import es.upm.miw.betca_tpv_core.domain.rest.GitHubService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("gitHubClient")
public class GitHubServiceRest implements GitHubService {

    private String gitHubUri;
    private String gitHubOwner;
    private String gitHubRepo;
    private String gitHubAPIKey;
    private WebClient.Builder webClientBuilder;

    @Autowired
    public GitHubServiceRest(
            @Value("${miw.tpv.github.uri}") String gitHubUri,
            @Value("${miw.tpv.github.owner}") String gitHubOwner,
            @Value("${miw.tpv.github.repo}") String gitHubRepo,
            @Value("${miw.tpv.github.apikey}") String gitHubAPIKey,
            WebClient.Builder webClientBuilder
    ) {
        this.gitHubUri = gitHubUri;
        this.gitHubOwner = gitHubOwner;
        this.gitHubRepo = gitHubRepo;
        this.gitHubAPIKey = gitHubAPIKey;
        this.webClientBuilder = webClientBuilder;
    }

    // https://docs.github.com/en/rest/reference/issues#create-an-issue
    @Override
    public Mono<Issue> create(Issue issue) {
        return null; // TODO implement
    }

    // https://docs.github.com/en/rest/reference/issues#get-an-issue
    @Override
    public Mono<Issue> read(Integer number) {
        return this.webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(gitHubUri)
                        .path("/repos/" + gitHubOwner + "/" + gitHubRepo + "/issues/" + number)
                        .build())
                .exchange()
                .onErrorResume(exception ->
                        Mono.error(new BadGatewayException("Unexpected error. GitHub Service. " + exception.getMessage())))
                .flatMap(response -> {
                    if (HttpStatus.UNAUTHORIZED.equals(response.statusCode())) {
                        return Mono.error(new ForbiddenException("Forbidden: GitHub Issue Details"));
                    } else if (HttpStatus.NOT_FOUND.equals(response.statusCode())) {
                        return Mono.error(new NotFoundException("Not Found: GitHub Issue Details"));
                    } else if (response.statusCode().isError()) {
                        return Mono.error(new BadGatewayException("Unexpected error: GitHub Service."));
                    } else {
                        return response.bodyToMono(Issue.class);
                    }
                });
    }

    // https://docs.github.com/en/rest/reference/issues#list-repository-issues
    @Override
    public Flux<Issue> search(String labels, String state, String assignee) {
        String query = "state=" + state
                + (!assignee.equals("") ? "&assignee=" + assignee : "")
                + (!labels.equals("") ? "&labels=" + labels : "");

        return this.webClientBuilder.build()
                //.mutate()
                //.defaultHeader("Authorization", "Basic " + gitHubOwner + ":" + gitHubAPIKey).build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host(gitHubUri)
                        .path("/repos/" + gitHubOwner + "/" + gitHubRepo + "/issues")
                        .query(query)
                        .build())
                .exchange()
                .onErrorResume(exception ->
                        Mono.error(new BadGatewayException("Unexpected error. GitHub Service. " + exception.getMessage())))
                .flatMapMany(response -> {
                    if (HttpStatus.UNAUTHORIZED.equals(response.statusCode())) {
                        return Mono.error(new ForbiddenException("Forbidden: GitHub Issue Search"));
                    } else if (HttpStatus.NOT_FOUND.equals(response.statusCode())) {
                        return Mono.error(new NotFoundException("Not Found: GitHub Issue Search"));
                    } else if (response.statusCode().isError()) {
                        return Mono.error(new BadGatewayException("Unexpected error: GitHub Service."));
                    } else {
                        return response.bodyToFlux(Issue.class);
                    }
                });
    }

}
