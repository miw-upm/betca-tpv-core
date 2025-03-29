package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.Tags;
import es.upm.miw.betca_tpv_core.domain.persistence.TagsPersistence;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(SpringExtension.class)  // Para trabajar con el contexto de Spring
@SpringBootTest  // Inicia el contexto de la aplicación para las pruebas de integración
public class TagsServiceIT {

    @Autowired
    private TagsService tagsService;

    @Autowired
    private TagsPersistence tagsPersistence; // Este sería el repositorio real, no un mock

    private Tags tag;

    @BeforeEach
    void setUp() {
        // Inicializar la etiqueta para las pruebas
        tag = new Tags("Technology", "IT", "Programming-related tags");
        // Opcionalmente, se puede guardar en el repositorio antes de cada prueba si es necesario
        tagsPersistence.create(tag).block();
    }

    @Test
    void testCreate() {
        Tags newTag = new Tags("Science", "Research", "Research-related tags");

        Mono<Tags> result = tagsService.create(newTag);

        StepVerifier.create(result)
                .expectNextMatches(createdTag -> "Science".equals(createdTag.getName()))
                .verifyComplete();
    }

    @Test
    void testReadByName() {
        Mono<Tags> result = tagsService.readByName("Technology");

        StepVerifier.create(result)
                .expectNextMatches(readTag -> "Technology".equals(readTag.getName()))
                .verifyComplete();
    }

    @Test
    void testUpdate() {
        Tags updatedTag = new Tags("Technology", "IT", "Updated description");

        Mono<Tags> result = tagsService.update("Technology", updatedTag);

        StepVerifier.create(result)
                .expectNextMatches(tag1 -> "Updated description".equals(tag1.getDescription()))
                .verifyComplete();
    }

    @Test
    void testDeleteByName() {
        Mono<Void> result = tagsService.deleteByName("Technology");

        StepVerifier.create(result)
                .verifyComplete(); // Solo verificar que se complete sin errores
    }

    @Test
    void testFindByAnyNullField() {
        Mono<Tags> result = tagsService.findByAnyNullField().next();

        StepVerifier.create(result)
                .expectNextMatches(tag -> tag.getName().equals("Technology"))
                .verifyComplete();
    }

    @Test
    void testFindById() {
        Mono<Tags> result = tagsService.findById(tag.getId());

        StepVerifier.create(result)
                .expectNextMatches(t -> "Technology".equals(t.getName()))
                .verifyComplete();
    }

    @Test
    void testDeleteById() {
        Mono<Void> result = tagsService.deleteById(tag.getId());

        StepVerifier.create(result)
                .verifyComplete();
    }
}
