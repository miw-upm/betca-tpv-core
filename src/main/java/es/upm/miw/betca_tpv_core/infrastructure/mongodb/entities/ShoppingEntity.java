package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Shopping;
import es.upm.miw.betca_tpv_core.domain.model.ShoppingState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingEntity {
    @DBRef
    private ArticleEntity articleEntity;
    private String description;
    private BigDecimal retailPrice;
    private Integer amount;
    private BigDecimal discount;
    private ShoppingState state;

    public ShoppingEntity(Shopping shopping) {
        BeanUtils.copyProperties(shopping, this);
    }

    public Shopping toShopping() {
        Shopping shopping = new Shopping();
        BeanUtils.copyProperties(this, shopping);
        shopping.setBarcode(this.articleEntity.getBarcode());
        return shopping;
    }

}
