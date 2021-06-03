package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Salespeople;
import es.upm.miw.betca_tpv_core.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Data
@NoArgsConstructor
@Document
@Builder
@AllArgsConstructor
public class SalespeopleEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private int SalespeopleId;
    //second commit
    private LocalDate salesDate;

    @DBRef(lazy = true)
    private TicketEntity ticketEntity;


    public SalespeopleEntity(Salespeople salespeople, TicketEntity ticketEntity) {
        BeanUtils.copyProperties(salespeople, this);
        this.ticketEntity = ticketEntity;
    }

    public Salespeople toSalespeople() {
        Salespeople salespeople = new Salespeople();
        BeanUtils.copyProperties(this, salespeople);
        if (Objects.nonNull(this.getTicketEntity())) {
            salespeople.setTicketId(this.getTicketEntity().getId());

            salespeople.setArticleBarcode(this.getTicketEntity().getShoppingEntityList().stream()
                    .map(shoppingEntity -> shoppingEntity.getArticleEntity().getBarcode()).toArray(String[]::new));

            salespeople.setAmount(this.getTicketEntity().getShoppingEntityList().stream()
                    .mapToInt(ShoppingEntity::getAmount).sum());

            salespeople.setTotal(this.getTicketEntity().getShoppingEntityList().stream()
                    .map(shoppingEntity -> shoppingEntity.getRetailPrice()).reduce(BigDecimal.ZERO, BigDecimal::add));
        }
        return salespeople;
    }
}
