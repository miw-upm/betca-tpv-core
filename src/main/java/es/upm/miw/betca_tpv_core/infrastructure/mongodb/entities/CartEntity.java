package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Cart;
import es.upm.miw.betca_tpv_core.domain.model.CartState;
import es.upm.miw.betca_tpv_core.domain.model.Tax;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartEntity {
    @DBRef
    private ArticleEntity articleEntity;
    private String photo;
    private String description;
    private BigDecimal retailPrice;
    private Integer amount;
    private BigDecimal discount;
    private CartState state;

    public CartEntity(Cart cart) {
        BeanUtils.copyProperties(cart, this);
    }

    public Cart toCart() {
        Cart cart = new Cart();
        BeanUtils.copyProperties(this, cart);
        cart.setBarcode(this.articleEntity.getBarcode());
        return cart;
    }

    public BigDecimal baseTaxValue() {
        Tax taxArticle = articleEntity.getTax();
        BigDecimal taxValue = taxArticle != null ? taxArticle.getRate() : BigDecimal.ZERO;
        return retailPrice.divide( BigDecimal.ONE.add(taxValue.divide(new BigDecimal("100"))), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal taxValue() {
        return retailPrice.subtract(baseTaxValue());
    }
}
