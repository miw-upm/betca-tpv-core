package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Ticket;
import es.upm.miw.betca_tpv_core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class TicketEntity {
    @Id
    private String id;
    private String reference;
    private List< ShoppingEntity > shoppingEntityList;
    private LocalDateTime creationDate;
    private BigDecimal cash;
    private BigDecimal card;
    private BigDecimal voucher;
    private String note;
    private String userMobile;

    public TicketEntity(Ticket ticket) {
        BeanUtils.copyProperties(ticket, this);
        if (Objects.nonNull(ticket.getUser())) {
            this.userMobile = ticket.getUser().getMobile();
        }
        this.shoppingEntityList = new ArrayList<>();
    }

    public void add(ShoppingEntity shoppingEntity) {
        this.shoppingEntityList.add(shoppingEntity);
    }

    public Ticket toTicket() {
        Ticket ticket = new Ticket();
        BeanUtils.copyProperties(this, ticket);
        ticket.setShoppingList(this.getShoppingEntityList().stream()
                .map(ShoppingEntity::toShopping)
                .collect(Collectors.toList())
        );
        if (Objects.nonNull(this.userMobile)) {
            ticket.setUser(User.builder().mobile(this.getUserMobile()).build());
        }
        return ticket;
    }

}
