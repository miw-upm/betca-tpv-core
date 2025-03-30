package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@TestConfig
class TicketPersistenceMongodbIT {

    @Autowired
    private TicketPersistenceMongodb ticketPersistenceMongodb;
    @Autowired
    private GiftTicketPersistenceMongodb giftTicketPersistenceMongodb;

    @Test
    void tesCreate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").amount(2)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Ticket ticket = Ticket.builder().reference("RyR_8_SkT9igCikrWWWGkQ").cash(new BigDecimal("200"))
                .card(BigDecimal.ZERO).voucher(BigDecimal.ZERO).note("note").creationDate(LocalDateTime.now())
                .shoppingList(List.of(shopping1, shopping2)).build();
        StepVerifier
                .create(this.ticketPersistenceMongodb.create(ticket))
                .expectNextMatches(dbTicket -> {
                    assertNotNull(dbTicket.getId());
                    assertNotNull(dbTicket.getCreationDate());
                    assertEquals("RyR_8_SkT9igCikrWWWGkQ", dbTicket.getReference());
                    assertEquals(2, dbTicket.getShoppingList().size());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testReadReceiptByReference() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").amount(2)
                .discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").amount(3)
                .discount(BigDecimal.TEN).state(ShoppingState.NOT_COMMITTED).build();
        Ticket ticket = Ticket.builder().reference("RyR_8_SkT9igCikrWWWGkQ").cash(new BigDecimal("200"))
                .card(BigDecimal.ZERO).voucher(BigDecimal.ZERO).note("note").creationDate(LocalDateTime.now())
                .shoppingList(List.of(shopping1, shopping2)).build();
        /*POST ticket*/
        Ticket ticketTest = this.ticketPersistenceMongodb.create(ticket).block();
        assertNotNull(ticketTest);
        assertNotNull(ticketTest.getId());
        assertNotNull(ticketTest.getCreationDate());
        /*GET ticket*/
        GiftTicket giftTicket = new GiftTicket(ticketTest.getId(), ticketTest);
        giftTicket.setReference(UUIDBase64.URL.encode());
        /*POST gift ticket*/
        GiftTicket giftTicketTest = this.giftTicketPersistenceMongodb.create(giftTicket).block();
        assertNotNull(giftTicketTest);
        assertNotNull(giftTicketTest.getId());
        assertNotNull(giftTicketTest.getReference());
        /*GET ticket*/
        StepVerifier
                .create(this.giftTicketPersistenceMongodb.getGiftTicketByReference(giftTicketTest.getReference()))
                .expectNextMatches(dbGiftTicket -> {
                    assertNotNull(dbGiftTicket.getId());
                    assertNotNull(dbGiftTicket.getMessage());
                    assertNotNull(dbGiftTicket.getTicket());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void test_findbyUserMobile(){
        StepVerifier
                .create(this.ticketPersistenceMongodb.findByUserMobile("666666004"))
                .expectNextCount(2)
                .thenConsumeWhile(Objects::nonNull)
                .verifyComplete();
    }

    @Test
    void test_findbyUserMobile_UserWithOutPurchasedBarcodes(){
        StepVerifier
                .create(this.ticketPersistenceMongodb.findByUserMobile("666666001"))
                .expectNextCount(0)
                .verifyComplete();
    }
}
