package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Tag;
import es.upm.miw.betca_tpv_core.domain.services.TagService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@Rest
@RequestMapping(TagResource.TAGS)
public class TagResource {
    public static final String TAGS = "/tags";
    public static final String NAME_ID = "/{name}";
    public static final String SEARCH = "/search";

    private final TagService tagService;

    @Autowired
    public TagResource(TagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Tag> create(@Valid @RequestBody Tag tag) {
        tag.doDefault();
        return this.tagService.create(tag);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(NAME_ID)
    public Mono<Tag> read(@PathVariable String name) {
        return this.tagService.read(name);
    }

    @PutMapping(NAME_ID)
    public Mono<Tag> update(@PathVariable String name, @Valid @RequestBody Tag tag) {
        return this.tagService.update(name, tag);
    }

    @GetMapping(SEARCH)
    public Flux<Tag> findByBarcodeAndDescriptionAndReferenceAndStockLessThanAndDiscontinuedNullSafe(
            @RequestParam(required = false) String name, @RequestParam(required = false) String group, @
            RequestParam(required = false) String description) {
        return this.tagService.findByNameAndGroupAndDescriptionNullSafe(
                name, group, description)
                .map(Tag::ofNameGroupDescription);
    }
}
