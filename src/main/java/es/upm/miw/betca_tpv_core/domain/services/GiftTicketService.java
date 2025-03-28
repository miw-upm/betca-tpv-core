package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.persistence.GiftTicketPersistence;
import es.upm.miw.betca_tpv_core.domain.persistence.TicketPersistence;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfGiftTicketBuilder;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.GiftTicketDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class GiftTicketService {

    private final GiftTicketPersistence giftTicketPersistence;
    private final TicketPersistence ticketPersistence;

    @Autowired
    public GiftTicketService(GiftTicketPersistence giftTicketPersistence, TicketPersistence ticketPersistence) {
        this.giftTicketPersistence = giftTicketPersistence;
        this.ticketPersistence = ticketPersistence;
    }

    public Mono<GiftTicket> create(GiftTicketDto giftTicketDto) {
        if (Objects.equals(giftTicketDto.getId(), "0")) {
            return Mono.error(new NotFoundException("Ticket not found with ID: " + giftTicketDto.getId()));
        }
        return this.ticketPersistence.readById(giftTicketDto.getId())
                .flatMap(ticket -> {
                    GiftTicket giftTicket = new GiftTicket(giftTicketDto.getMessage(), ticket);
                    giftTicket.setId(null);
                    giftTicket.setReference(UUIDBase64.URL.encode());
                    giftTicket.setTicket(ticket);
                    return this.giftTicketPersistence.create(giftTicket)
                            .map(createdGiftTicket -> createdGiftTicket);
                });
    }

    public Mono<byte[]> readForReceiptByReference(String reference) {
        var monoTicket = this.giftTicketPersistence.getGiftTicketByReference(reference);
        return monoTicket.map(new PdfGiftTicketBuilder()::generateGiftTicket);
    }
}