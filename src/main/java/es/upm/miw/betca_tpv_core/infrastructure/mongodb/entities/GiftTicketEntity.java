package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.GiftTicket;
import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public GiftTicketEntity(GiftTicket giftTicket) {
        BeanUtils.copyProperties(giftTicket, this);
    }

    public GiftTicket toGiftTicket() {
        GiftTicket giftTicket = new GiftTicket();
        BeanUtils.copyProperties(this, giftTicket);
        return giftTicket;
    }

}
