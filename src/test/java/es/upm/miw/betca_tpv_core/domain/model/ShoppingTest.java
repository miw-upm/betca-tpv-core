package es.upm.miw.betca_tpv_core.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ShoppingTest {

    @Test
    void testTotalUnit() {
        Shopping shopping = Shopping.builder().barcode("1").description("d").amount(5)
                .retailPrice(new BigDecimal("7.13")).discount(new BigDecimal("5.17"))
                .state(ShoppingState.COMMITTED).build();
        assertEquals(0, new BigDecimal("6.76").compareTo(shopping.totalUnit()));
        assertEquals(0, new BigDecimal("33.80").compareTo(shopping.totalShopping()));
    }
}
