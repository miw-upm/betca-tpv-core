package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class CreditSaleEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    private Boolean payed;

    @DBRef(lazy = true)
    private TicketEntity ticketEntity;

    public CreditSaleEntity(CreditSale creditSale, TicketEntity ticketEntity) {
        BeanUtils.copyProperties(creditSale, this);
        this.ticketEntity = ticketEntity;
    }

    public CreditSale toCreditSale() {
        CreditSale creditSale = new CreditSale();
        BeanUtils.copyProperties(this, creditSale);
        if (Objects.nonNull(this.getTicketEntity())) {
            creditSale.setTicketReference(this.getTicketEntity().getReference());
        }
        return creditSale;
    }

    public CreditSaleEntity[] toCreditSaleArray() {
        CreditSaleEntity[] creditSaleEntityArray = new CreditSaleEntity[1];
        creditSaleEntityArray[0] = new CreditSaleEntity();
        BeanUtils.copyProperties(this, creditSaleEntityArray[0]);
        if(Objects.nonNull(this.getTicketEntity())) {
            creditSaleEntityArray[0].setTicketEntity((this.getTicketEntity()));
        }
        return creditSaleEntityArray;
    }
}
