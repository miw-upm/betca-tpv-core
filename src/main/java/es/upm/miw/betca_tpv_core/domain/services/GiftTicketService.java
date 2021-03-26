package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.persistence.GiftTicketPersistence;
import es.upm.miw.betca_tpv_core.domain.services.utils.PdfGiftTicketBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class GiftTicketService {
    private GiftTicketPersistence giftTicketPersistence;

    @Autowired
    public GiftTicketService(GiftTicketPersistence giftTicketPersistence) {
        this.giftTicketPersistence = giftTicketPersistence;
    }

    public Mono<GiftTicket> create(GiftTicket giftTicket) {
        return giftTicketPersistence.create(giftTicket);
    }

    public Mono< byte[] > readReceipt(String id) {
        return this.giftTicketPersistence.readById(id)
                .map(new PdfGiftTicketBuilder()::generateGiftTicket);
    }
}
