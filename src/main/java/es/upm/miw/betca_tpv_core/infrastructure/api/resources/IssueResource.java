package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Issue;
import es.upm.miw.betca_tpv_core.domain.services.IssueService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(IssueResource.ISSUES)
public class IssueResource {
    public static final String ISSUES = "/issues";
    public static final String SEARCH = "/search";
    public static final String ISSUE_NUMBER = "/{number}";

    private IssueService issueService;

    @Autowired
    public IssueResource(IssueService issueService) {
        this.issueService = issueService;
    }

    @GetMapping(ISSUE_NUMBER)
    public Mono<Issue> read(@PathVariable Integer number) {
        return this.issueService.read(number);
    }

    @GetMapping(SEARCH)
    public Flux<Issue> findByTitleAndBodyAndLabelsAndStateAndMilestoneAndAssigneeNullSafe(
            @RequestParam(required = false) String title, @RequestParam(required = false) String body,
            @RequestParam(required = false) String labels, @RequestParam(required = false) String state,
            @RequestParam(required = false) String milestone, @RequestParam(required = false) String assignee
    ) {
        return this.issueService.findByTitleAndBodyAndLabelsAndStateAndMilestoneAndAssigneeNullSafe(
                title == null ? "" : title, body == null ? "" : body,
                labels == null ? "" : labels, state == null ? "" : state,
                milestone == null ? "" : milestone, assignee == null ? "" : assignee
        ).map(Issue::ofTitleBodyLabels);
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Issue> create(@Valid @RequestBody Issue issue) {
        issue.doDefault();
        return this.issueService.create(issue);
    }

}
