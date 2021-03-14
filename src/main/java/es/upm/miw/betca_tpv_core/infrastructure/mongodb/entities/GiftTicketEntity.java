package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class GiftTicketEntity {
    @Id
    private String id;
    private String message;
    @DBRef
    private TicketEntity ticketEntity;

    public GiftTicketEntity(GiftTicket giftTicket, TicketEntity ticketEntity) {
        BeanUtils.copyProperties(giftTicket, this);
        this.ticketEntity = ticketEntity;
    }

    public GiftTicket toGiftTicket() {
        GiftTicket giftTicket = new GiftTicket();
        BeanUtils.copyProperties(this, giftTicket);
        if(Objects.nonNull(this.getTicketEntity())){
            giftTicket.setTicketId(this.getTicketEntity().getId());
        }
        return giftTicket;
    }

}
