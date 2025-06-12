package es.upm.miw.betca_tpv_core.infrastructure.api.resources;


import es.upm.miw.betca_tpv_core.domain.model.Tags;
import es.upm.miw.betca_tpv_core.domain.services.TagsService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Rest
@RequestMapping(TagsResource.TAGS)
public class TagsResource {
   public static final String TAGS = "/tags";
    public static final String TAG_ID = "/{id}";
    public static final String SEARCH = "/search";
    public static final String SEARCH_BY_GROUP = "/search-by-group";
    public static final String ARTICLES = "/articles";

    private final TagService tagService;
    private final ArticleService articleService;

    @Autowired
    public TagResource(TagService tagService, ArticleService articleService) {
        this.tagService = tagService;
        this.articleService = articleService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Tag> create(@Valid @RequestBody Tag tag) {
        return this.tagService.create(tag);
    }

    @GetMapping(TAG_ID)
    public Mono<Tag> read(@PathVariable String id) {
        return this.tagService.read(id);
    }

    @GetMapping
    public Flux<Tag> findAll() {
        return this.tagService.findAll();
    }

    @GetMapping(SEARCH)
    public Flux<Tag> findByName(@RequestParam String name) {
        return this.tagService.findByName(name);
    }

    @GetMapping(SEARCH_BY_GROUP)
    public Flux<Tag> findByGroup(@RequestParam String group) {
        return this.tagService.findByGroup(group);
    }

    @PutMapping(TAG_ID)
    public Mono<Tag> update(@PathVariable String id, @Valid @RequestBody Tag tag) {
        return this.tagService.update(id, tag);
    }

    @DeleteMapping(TAG_ID)
    public Mono<Void> delete(@PathVariable String id) {
        return this.tagService.delete(id);
    }

    @GetMapping(TAG_ID + ARTICLES)
    public Flux<Article> findArticlesByTagId(@PathVariable String id) {
        return this.articleService.findByTag(id);
    }


}
