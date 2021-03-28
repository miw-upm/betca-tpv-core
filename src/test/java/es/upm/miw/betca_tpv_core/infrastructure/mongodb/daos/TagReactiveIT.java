package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
public class TagReactiveIT {

    @Autowired
    private TagReactive tagReactive;

    @Test
    void testFindByName() {
        StepVerifier.
                create(this.tagReactive.findByName("name1")).
                expectNextMatches(tag -> {
                    System.out.println("tag: " + tag);
                    assertTrue(tag.getName().contains("name1"));
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindBNameAndGroupAndDescriptionNullSafe() {
        StepVerifier
                .create(this.tagReactive.findBNameAndGroupAndDescriptionNullSafe(
                        null, "group1", null))
                .expectNextMatches(tag -> {
                    assertTrue(tag.getGroup().contains("group1"));

                    return true;
                })
                .thenCancel()
                .verify();
    }
}
