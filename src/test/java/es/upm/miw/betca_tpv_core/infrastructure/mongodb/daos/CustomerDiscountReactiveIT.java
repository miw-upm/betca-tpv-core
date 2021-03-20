package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
public class CustomerDiscountReactiveIT {

    @Autowired
    private CustomerDiscountReactive customerDiscountReactive;

    @Test
    void testFindByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe() {
        StepVerifier
                .create(this.customerDiscountReactive.findByNoteAndDiscountAndMinimumPurchaseAndUserNullSafe(null, null, null, null))
                .expectNextMatches(customerDiscountEntity -> {
                    assertEquals("66", customerDiscountEntity.getUser());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testFindByUser() {
        StepVerifier
                .create(this.customerDiscountReactive.findByUser("66"))
                .expectNextMatches(customerDiscountEntity -> {
                    assertEquals("discount1", customerDiscountEntity.getNote());
                    assertEquals(30.0, customerDiscountEntity.getDiscount());
                    assertEquals(50.0, customerDiscountEntity.getMinimumPurchase());
                    return true;
                })
                .thenCancel()
                .verify();
    }
}
