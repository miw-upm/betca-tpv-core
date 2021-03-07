package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import es.upm.miw.betca_tpv_core.domain.model.CreditSale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class CreditEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    private String userReference;
    private CreditSaleEntity[] creditSaleEntities;

    public CreditEntity(Credit credit, CreditSaleEntity[] creditSaleEntities) {
        BeanUtils.copyProperties(credit, this);
        this.creditSaleEntities = creditSaleEntities;
    }

    public Credit toCredit() {
        Credit credit = new Credit();
        BeanUtils.copyProperties(this, credit);
        if (Objects.nonNull(this.getCreditSaleEntities())) {
            CreditSale[] creditSales = new CreditSale[this.getCreditSaleEntities().length];
            for( int i = 0; i < this.getCreditSaleEntities().length; i++){
                creditSales[i] = this.getCreditSaleEntities()[i].toCreditSale();
            }
            credit.setCreditSales(creditSales);
        }
        return credit;
    }
}
