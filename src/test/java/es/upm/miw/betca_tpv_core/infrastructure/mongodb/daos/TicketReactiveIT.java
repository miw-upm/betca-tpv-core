package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfig
public class TicketReactiveIT {

    @Autowired
    private TicketReactive ticketReactive;

    @Test
    void testFindByReference() {
        StepVerifier
                .create(this.ticketReactive.findByReference("nUs81zZ4R_iuoq0_zCRm6A"))
                .expectNextMatches(ticket -> {
                    assertEquals("5fa45e863d6e834d642689ac", ticket.getId());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByReferenceLikeOrUserMobileLike() {
        StepVerifier
                .create(this.ticketReactive.findByReferenceLikeOrUserMobileLikeNullSafe("005", "005"))
                .expectNextMatches(ticket -> {
                    System.out.println(ticket);
                    assertTrue(ticket.getUserMobile().contains("005"));
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByReferenceLikeOrUserMobileLikeNullSafe() {
        StepVerifier
                .create(this.ticketReactive.findByReferenceLikeOrUserMobileLikeNullSafe("", ""))
                .assertNext(Assertions::assertNotNull)
                .thenCancel()
                .verify();
    }

    @Test
    void testFindById(){
        StepVerifier
                .create(this.ticketReactive.findById("5fa45e863d6e834d642689ac"))
                .expectNextMatches(ticket -> {
                    assertEquals("5fa45e863d6e834d642689ac", ticket.getId());
                    return true;
                })
                .thenCancel()
                .verify();
    }
    @Test
    void testFindByCreationDateBetween(){
        LocalDateTime dateIni = LocalDateTime.of(2019, Month.JANUARY, 01, 00, 00, 00);
        LocalDateTime dateEnd = LocalDateTime.of(2019, Month.JANUARY, 15, 00, 00, 00);

        StepVerifier
                .create(this.ticketReactive.findByCreationDateBetween(dateIni,dateEnd))
                .expectNextMatches(ticket -> {
                    assertTrue(ticket.getCreationDate().isAfter(dateIni) && ticket.getCreationDate().isBefore(dateEnd));
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUserMobile() {
        List<String> ticketsIds = List.of(
                "5fa4603b7513a164chop77ac", "5gfaw03b7513a164chop77ac",
                "7faw03b7513a164chop77ac", "9jfaw03b7513a164chop77ac"
        );
        StepVerifier
                .create(this.ticketReactive.findByUserMobile("66"))
                .assertNext(ticketEntity -> assertTrue(ticketsIds.contains(ticketEntity.getId())))
                .assertNext(ticketEntity -> assertTrue(ticketsIds.contains(ticketEntity.getId())))
                .assertNext(ticketEntity -> assertTrue(ticketsIds.contains(ticketEntity.getId())))
                .assertNext(ticketEntity -> assertTrue(ticketsIds.contains(ticketEntity.getId())))
                .thenCancel()
                .verify();
    }

}
