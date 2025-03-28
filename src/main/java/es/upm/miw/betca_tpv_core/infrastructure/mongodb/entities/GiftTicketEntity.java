package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class GiftTicketEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    private String message;
    private TicketEntity ticket;

    public GiftTicketEntity(GiftTicket giftTicket) {
        BeanUtils.copyProperties(giftTicket, this);
        if (Objects.nonNull(giftTicket.getTicket())) {
            this.ticket = new TicketEntity(giftTicket.getTicket());
            if (giftTicket.getTicket().getId() == null) {
                throw new IllegalStateException("Cannot create GiftTicketEntity: Ticket ID is null");
            }
        }
    }


    public GiftTicket toGiftTicket(){
        GiftTicket giftTicket = new GiftTicket();
        BeanUtils.copyProperties(this, giftTicket);

        giftTicket.setTicket(this.ticket.toTicket());
        return giftTicket;
    }
}