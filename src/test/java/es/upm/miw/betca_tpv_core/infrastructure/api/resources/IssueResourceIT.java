package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Issue;
import es.upm.miw.betca_tpv_core.domain.model.IssueAssignee;
import es.upm.miw.betca_tpv_core.domain.model.IssueLabel;
import es.upm.miw.betca_tpv_core.domain.model.IssueMilestone;
import es.upm.miw.betca_tpv_core.domain.rest.GitHubService;
import es.upm.miw.betca_tpv_core.infrastructure.api.RestClientTestService;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.IssueBasicDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.IssueDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.IssueResource.ISSUES;
import static es.upm.miw.betca_tpv_core.infrastructure.api.resources.IssueResource.SEARCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

@RestTestConfig
public class IssueResourceIT {

    @Autowired
    private WebTestClient webTestClient;
    @Autowired
    private RestClientTestService restClientTestService;
    @MockBean
    private GitHubService gitHubService;

    @BeforeEach
    void mocks() {
        BDDMockito.given(
                this.gitHubService.search(anyString(), anyString(), anyString())
        ).willAnswer(arguments -> Flux.just(
                Issue.builder()
                        .title("Found a bug")
                        .body("I'm having a problem with this.")
                        .labels(new IssueLabel[]{IssueLabel.builder().name("bug").build()})
                        .state("open")
                        .milestone(IssueMilestone.builder().title("v1.0").build())
                        .assignee(IssueAssignee.builder().login("Kazlunn").build())
                        .build(),
                Issue.builder()
                        .title("This is a test title.")
                        .body("This could be improved.")
                        .labels(new IssueLabel[]{IssueLabel.builder().name("enhancement").build()})
                        .state("open")
                        .milestone(IssueMilestone.builder().title("v1.1").build())
                        .assignee(IssueAssignee.builder().login("octocat").build())
                        .build()
        ));

        BDDMockito.given(
                this.gitHubService.read(anyInt())
        ).willAnswer(arguments -> Mono.just(
                Issue.builder()
                        .number(10)
                        .title("Found a bug")
                        .body("I'm having a problem with this.")
                        .labels(new IssueLabel[] {
                                IssueLabel.builder().name("bug").build(),
                                IssueLabel.builder().name("high priority").build()
                        })
                        .state("open")
                        .milestone(IssueMilestone.builder().title("v1.0").build())
                        .assignee(IssueAssignee.builder().login("octocat").build())
                        .build()
        ));
    }

    @Test
    void testFindByTitleAndBodyAndLabelsAndStateAndMilestoneAndAssigneeNullSafe() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ISSUES + SEARCH)
                        .queryParam("title", "bug")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(IssueBasicDto.class)
                .value(issueBasicDtos ->
                        assertTrue(issueBasicDtos.stream()
                                .anyMatch(issueBasicDto -> issueBasicDto.getTitle().contains("bug")))
                );
    }

    @Test
    void testReadByNumber() {
        this.restClientTestService.loginAdmin(webTestClient)
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(ISSUES + "/" + 10)
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(IssueDto.class)
                .value(issueDto -> {
                    assertEquals(10, issueDto.getNumber());
                    assertEquals("Found a bug", issueDto.getTitle());
                    assertEquals("I'm having a problem with this.", issueDto.getBody());
                    assertEquals("bug,high priority", issueDto.getLabels());
                    assertEquals("open", issueDto.getState());
                    assertEquals("v1.0", issueDto.getMilestone());
                    assertEquals("octocat", issueDto.getAssignee());
                });
    }

}
