package es.upm.miw.betca_tpv_core.infrastructure.rest_client;

import es.upm.miw.betca_tpv_core.domain.exceptions.BadGatewayException;
import es.upm.miw.betca_tpv_core.domain.exceptions.ForbiddenException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Issue;
import es.upm.miw.betca_tpv_core.domain.rest.GitHubService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.IssueCreationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service("gitHubClient")
public class GitHubServiceRest implements GitHubService {

    public static final String HTTPS = "https";
    public static final String REPOS = "/repos/";
    public static final String ISSUES = "/issues";
    public static final String UNEXPECTED_ERROR = "Unexpected error. GitHub Service.";

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

    // https://docs.github.com/en/rest/reference/issues#get-an-issue
    @Override
    public Mono<Issue> read(Integer number) {
        return this.webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(gitHubUri)
                        .path(REPOS + gitHubOwner + "/" + gitHubRepo + ISSUES + "/" + number)
                        .build())
                .exchange()
                .onErrorResume(exception ->
                        Mono.error(new BadGatewayException(UNEXPECTED_ERROR + exception.getMessage())))
                .flatMap(response -> {
                    if (HttpStatus.UNAUTHORIZED.equals(response.statusCode())) {
                        return Mono.error(new ForbiddenException("Forbidden: GitHub Issue Details"));
                    } else if (HttpStatus.NOT_FOUND.equals(response.statusCode())) {
                        return Mono.error(new NotFoundException("Not Found: GitHub Issue Details"));
                    } else if (response.statusCode().isError()) {
                        return Mono.error(new BadGatewayException(UNEXPECTED_ERROR));
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
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(gitHubUri)
                        .path(REPOS + gitHubOwner + "/" + gitHubRepo + ISSUES)
                        .query(query)
                        .build())
                .exchange()
                .onErrorResume(exception ->
                        Mono.error(new BadGatewayException(UNEXPECTED_ERROR + exception.getMessage())))
                .flatMapMany(response -> {
                    if (HttpStatus.UNAUTHORIZED.equals(response.statusCode())) {
                        return Mono.error(new ForbiddenException("Forbidden: GitHub Issue Search"));
                    } else if (HttpStatus.NOT_FOUND.equals(response.statusCode())) {
                        return Mono.error(new NotFoundException("Not Found: GitHub Issue Search"));
                    } else if (response.statusCode().isError()) {
                        return Mono.error(new BadGatewayException(UNEXPECTED_ERROR));
                    } else {
                        return response.bodyToFlux(Issue.class);
                    }
                });
    }

    // https://docs.github.com/en/rest/reference/issues#create-an-issue
    @Override
    public Mono<Issue> create(IssueCreationDto issueCreationDto) {
        return this.webClientBuilder.build()
                .mutate().defaultHeader("Authorization", "token " + gitHubAPIKey).build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme(HTTPS)
                        .host(gitHubUri)
                        .path(REPOS + gitHubOwner + "/" + gitHubRepo + ISSUES)
                        .build())
                .body(Mono.just(issueCreationDto), IssueCreationDto.class)
                .exchange()
                .onErrorResume(exception ->
                        Mono.error(new BadGatewayException(UNEXPECTED_ERROR + exception.getMessage())))
                .flatMap(response -> {
                    if (HttpStatus.UNAUTHORIZED.equals(response.statusCode())) {
                        return Mono.error(new ForbiddenException("Forbidden: GitHub Issue Creation"));
                    } else if (HttpStatus.NOT_FOUND.equals(response.statusCode())) {
                        return Mono.error(new NotFoundException("Not Found: GitHub Issue Creation"));
                    } else if (response.statusCode().isError()) {
                        return Mono.error(new BadGatewayException(UNEXPECTED_ERROR));
                    } else {
                        return response.bodyToMono(Issue.class);
                    }
                });
    }

}
