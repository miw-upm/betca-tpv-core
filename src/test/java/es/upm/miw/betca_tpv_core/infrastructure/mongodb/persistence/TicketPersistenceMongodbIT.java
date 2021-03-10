package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.*;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ShoppingEntity;
import net.minidev.json.writer.BeansMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.math.BigDecimal.TEN;
import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class TicketPersistenceMongodbIT {

    @Autowired
    private TicketPersistenceMongodb ticketPersistenceMongodb;

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
    void testFindByIdOrReferenceLikeOrUserMobileLike() {
        StepVerifier
                .create(this.ticketPersistenceMongodb.findByIdOrReferenceLikeOrUserMobileLikeNullSafe("5fa45e863d6e834d642689ac"))
                .expectNextMatches(ticket -> {
                    assertEquals("nUs81zZ4R_iuoq0_zCRm6A", ticket.getReference());
                    assertEquals("5fa45e863d6e834d642689ac", ticket.getId());
                    assertEquals("666666000", ticket.getUser().getMobile());
                    return true;
                })
                .expectComplete()
                .verify();
    }

    @Test
    void testFindByIdOrReferenceLikeOrUserMobileLikeNullSafe() {
        StepVerifier
                .create(this.ticketPersistenceMongodb.findByIdOrReferenceLikeOrUserMobileLikeNullSafe(""))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByIdNotFound() {
        StepVerifier
                .create(this.ticketPersistenceMongodb.findById("kk"))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdateNotFound() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").description("Zarzuela - Falda T2")
                .retailPrice(new BigDecimal(20)).amount(5).discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").description("Zarzuela - Falda T4")
                .retailPrice(new BigDecimal("27.8")).amount(3).discount(new BigDecimal("50")).state(ShoppingState.IN_STOCK).build();
        List<Shopping> shoppingList = new ArrayList<>();
        shoppingList.add(shopping1);
        shoppingList.add(shopping2);
        StepVerifier
                .create(this.ticketPersistenceMongodb.update("kk", shoppingList))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdate() {
        Shopping shopping1 = Shopping.builder().barcode("8400000000017").description("Zarzuela - Falda T2")
                .retailPrice(new BigDecimal(20)).amount(5).discount(BigDecimal.ZERO).state(ShoppingState.COMMITTED).build();
        Shopping shopping2 = Shopping.builder().barcode("8400000000024").description("Zarzuela - Falda T4")
                .retailPrice(new BigDecimal("27.8")).amount(3).discount(new BigDecimal("50")).state(ShoppingState.IN_STOCK).build();
        List<Shopping> shoppingList = new ArrayList<>();
        shoppingList.add(shopping1);
        shoppingList.add(shopping2);
        StepVerifier
                .create(this.ticketPersistenceMongodb.update("5fa45e863d6e834d642689ac", shoppingList))
                .expectNextMatches(ticket -> {
                    assertNotNull(ticket.getShoppingList());
                    assertEquals(2, ticket.getShoppingList().size());
                    assertEquals(5, ticket.getShoppingList().get(0).getAmount());
                    assertEquals(ShoppingState.IN_STOCK, ticket.getShoppingList().get(1).getState());
                    return true;
                })
                .verifyComplete();
    }

}
