package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Message;
import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.MessageEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;


import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class MessengerPersistenceMongodbIT {

    @Autowired
    private MessengerPersistenceMongodb messengerPersistenceMongodb;

    @Test
    void testCreate() {
        Message newMessage = Message.builder()
                .subject("Message 1")
                .text("Message text 1")
                .userFrom("6")
                .userTo("666666001")
                .isRead(Boolean.FALSE)
                .build();
        newMessage.doDefault();

        StepVerifier
                .create(this.messengerPersistenceMongodb.create(newMessage))
                .expectNextMatches(message -> {
                    assertEquals("Message 1", message.getSubject());
                    assertEquals("Message text 1", message.getText());
                    assertEquals("6", message.getUserFrom());
                    assertEquals("666666001", message.getUserTo());
                    assertFalse(message.getIsRead());
                    assertNotNull(message.getCreationDate());

                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByUserFromNullSafe() {
        StepVerifier
                .create(this.messengerPersistenceMongodb.findByUserFromNullSafe(
                        User.builder()
                                .mobile("6")
                                .build()))
                .expectNextMatches(message -> {
                    assertEquals("Message 1", message.getSubject());
                    assertEquals("Message text 1", message.getText());
                    assertEquals("6", message.getUserFrom());
                    assertEquals("666666001", message.getUserTo());
                    assertTrue(message.getIsRead());
                    assertNotNull(message.getCreationDate());

                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByUserToNullSafe() {
        StepVerifier
                .create(this.messengerPersistenceMongodb.findByUserToNullSafe(
                        User.builder()
                                .mobile("6")
                                .build()))
                .expectNextMatches(message -> {
                    assertEquals("Message 2", message.getSubject());
                    assertEquals("Message text 2", message.getText());
                    assertEquals("666666001", message.getUserFrom());
                    assertEquals("6", message.getUserTo());
                    assertTrue(message.getIsRead());
                    assertNotNull(message.getCreationDate());

                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindNotReadMessages() {
        StepVerifier
                .create(this.messengerPersistenceMongodb.findNotReadMessages(
                        User.builder()
                            .mobile("666666001")
                            .build()))
                .expectNextMatches(message -> {
                    assertEquals("Message 4", message.getSubject());
                    assertEquals("Message text 4", message.getText());
                    assertEquals("6", message.getUserFrom());
                    assertEquals("666666001", message.getUserTo());
                    assertFalse(message.getIsRead());
                    assertNotNull(message.getCreationDate());

                    return true;
                })
                .expectComplete()
                .verify();
    }

}
