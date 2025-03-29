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
    public static final String NAME_ID = "/{name}";
    public static final String SEARCH = "/search";

    private final TagsService tagService;

    @Autowired
    public TagsResource(TagsService tagService) {
        this.tagService = tagService;
    }

    @PostMapping(produces = {"application/json"})
    public Mono<Tags> create(@Valid @RequestBody Tags tag) {
        return this.tagService.create(tag);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(NAME_ID)
    public Mono<Tags> read(@PathVariable String name) {
        return this.tagService.readByName(name);
    }

    @PutMapping(NAME_ID)
    public Mono<Tags> update(@PathVariable String name, @Valid @RequestBody Tags tag) {
        return this.tagService.update(name, tag);
    }

    @GetMapping(SEARCH)
    public Flux<Tags> search(@RequestParam(required = false) String name) {
        return this.tagService.findByNameLikeAndGroupIsNotNullNullSafe(name);
    }
    @DeleteMapping(NAME_ID)
    public Mono<Void> delete(@PathVariable String name) {
        return this.tagService.deleteByName(name);
    }
    @DeleteMapping("/{id}")
    public Mono<Void> deleteById(@PathVariable String id) {
        return this.tagService.deleteById(id);
    }


    @GetMapping(produces = {"application/json"})
    public Flux<Tags> getAll() {
        return this.tagService.findAll();
    }
    @GetMapping("/{id}")
    public Mono<Tags> findById(@PathVariable String id) {
        return this.tagService.findById(id);
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Tags>> updateTag(@PathVariable String id, @RequestBody Tags tag) {
        return tagService.update(id, tag)
                .map(updatedTag -> ResponseEntity.ok(updatedTag)) // Devuelve 200 OK con el objeto actualizado
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build())) // Si no se encuentra el ID, devuelve 404
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build())); // Manejo de errores
    }


}
