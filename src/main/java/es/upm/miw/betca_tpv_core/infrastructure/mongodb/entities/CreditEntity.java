package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Credit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
        return credit;
    }
}
