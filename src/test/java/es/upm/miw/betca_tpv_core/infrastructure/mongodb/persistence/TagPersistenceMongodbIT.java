package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class TagPersistenceMongodbIT {

    @Autowired
    private TagPersistenceMongodb tagPersistenceMongodb;

    @Test
    void testReadByName() {
        StepVerifier
                .create(this.tagPersistenceMongodb.readByName("name1"))
                .expectNextMatches(tag -> {
                    assertEquals("name1", tag.getName());
                    assertEquals("group1", tag.getGroup());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testCreateExistingName() {
        StepVerifier
                .create(this.tagPersistenceMongodb.create(
                        Tag.builder().name("name1").group("group2").description("description").articleList(List.of()).build()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testCreate() {
        StepVerifier
                .create(this.tagPersistenceMongodb.create(
                        Tag.builder().name("name4").group("group3").description("description").articleList(List.of()).build()))
                .expectNextMatches(tag -> {
                    assertEquals("name4", tag.getName());
                    assertEquals("group3", tag.getGroup());
                    assertEquals("description", tag.getDescription());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testUpdateExistingName() {
        StepVerifier
                .create(this.tagPersistenceMongodb.update("name1",
                        Tag.builder().name("name2").group("group1").description("description").articleList(List.of()).build()))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testUpdate() {
        StepVerifier
                .create(this.tagPersistenceMongodb.update("name3",
                        Tag.builder().name("name3").group("group1").description("description").articleList(List.of()).build()))
                .expectNextMatches(tag -> {
                    assertEquals("group1", tag.getGroup());
                    assertEquals("description", tag.getDescription());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testFindBNameAndGroupAndDescriptionNullSafe() {
        StepVerifier
                .create(this.tagPersistenceMongodb.findBNameAndGroupAndDescriptionNullSafe(null, "group1", null))
                .expectNextMatches(tag -> {
                    assertEquals("group1", tag.getGroup());
                    return true;
                })
                .thenCancel()
                .verify();
    }

}
