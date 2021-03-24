package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class MessengerReactiveIT {

    @Autowired
    private MessengerReactive messengerReactive;

    @Test
    void testFindByUserFrom() {
        StepVerifier
                .create(this.messengerReactive.findByUserFrom("6"))
                .expectNextMatches(messageEntity -> {
                    assertEquals("1", messageEntity.getId());
                    assertEquals("Message 1", messageEntity.getSubject());
                    assertEquals("Message text 1", messageEntity.getText());
                    assertEquals("6", messageEntity.getUserFrom());
                    assertEquals("666666001", messageEntity.getUserTo());
                    assertTrue(messageEntity.getIsRead());

                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserTo() {
        StepVerifier
                .create(this.messengerReactive.findByUserTo("666666001"))
                .expectNextMatches(messageEntity -> {
                    assertEquals("1", messageEntity.getId());
                    assertEquals("Message 1", messageEntity.getSubject());
                    assertEquals("Message text 1", messageEntity.getText());
                    assertEquals("6", messageEntity.getUserFrom());
                    assertEquals("666666001", messageEntity.getUserTo());
                    assertTrue(messageEntity.getIsRead());

                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserToAndIsRead() {
        StepVerifier
                .create(this.messengerReactive.findByUserToAndIsRead("66", Boolean.FALSE))
                .expectNextMatches(messageEntity -> {
                    assertEquals("3", messageEntity.getId());
                    assertEquals("Message 3", messageEntity.getSubject());
                    assertEquals("Message text 3", messageEntity.getText());
                    assertEquals("666666001", messageEntity.getUserFrom());
                    assertEquals("66", messageEntity.getUserTo());
                    assertFalse(messageEntity.getIsRead());

                    return true;
                })
                .thenCancel()
                .verify();
    }

}
