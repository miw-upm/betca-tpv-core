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

import java.util.ArrayList;
import java.util.List;
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
    private List<CreditSaleEntity> creditSaleEntities;

    public CreditEntity(Credit credit) {
        BeanUtils.copyProperties(credit, this);
        this.creditSaleEntities = new ArrayList<>();
    }

    public Credit toCredit() {
        Credit credit = new Credit();
        BeanUtils.copyProperties(this, credit);
        if (Objects.nonNull(this.getCreditSaleEntities())) {
            List<CreditSale> creditSales = new ArrayList<>();
            for( int i = 0; i < this.getCreditSaleEntities().size(); i++){
                creditSales.add(this.getCreditSaleEntities().get(i).toCreditSale());
            }
            credit.setCreditSales(creditSales);
        }
        return credit;
    }
}
