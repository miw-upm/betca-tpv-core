package es.upm.miw.betca_tpv_core.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartTest {
    @Test
    void testTotalUnit() {
        Cart cart = Cart.builder().barcode("1")
                .photo("https://static.zara.net/photos///2021/V/0/1/p/2753/025/712/2/w/375/2753025712_6_1_1.jpg?ts=1614009934404")
                .description("d").amount(5)
                .retailPrice(new BigDecimal("7.13")).discount(new BigDecimal("5.17"))
                .state(CartState.COMMITTED).build();
        assertEquals(0, new BigDecimal("6.76").compareTo(cart.totalUnit()));
        assertEquals(0, new BigDecimal("33.80").compareTo(cart.totalCart()));
    }
}
