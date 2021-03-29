package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@TestConfig
class ProviderInvoicePersistenceMongodbIT {

    @Autowired
    private ProviderInvoicePersistenceMongodb providerInvoicePersistenceMongodb;

    @Test
    void testFindAll() {
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.findAll())
                .expectNextMatches(providerInvoice -> {
                    assertNotNull(providerInvoice.getNumber());
                    return true;
                })
                .thenCancel()
                .verify();
    }

    @Test
    void testCreate() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder()
                .number(6666)
                .creationDate(LocalDate.of(2021, 12, 1))
                .baseTax(new BigDecimal("6000"))
                .taxValue(new BigDecimal("60"))
                .providerCompany("pro1")
                .orderId("ord6")
                .build();
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.create(providerInvoice))
                .expectNextMatches(createdProviderInvoice -> {
                    assertEquals(6666, providerInvoice.getNumber());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testCreateExistingNumber() {
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.create(
                        ProviderInvoice.builder().number(1111).providerCompany("pro1").build()
                ))
                .expectError(ConflictException.class)
                .verify();
    }

    @Test
    void testCreateNonExistingProvider() {
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.create(
                        ProviderInvoice.builder().number(999999).providerCompany("kk").build()
                ))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testReadNonExistingNumber() {
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.readByNumber(9999))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdate() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder()
                .number(1111)
                .creationDate(LocalDate.of(2021, 1, 1))
                .baseTax(new BigDecimal("1000"))
                .taxValue(new BigDecimal("10"))
                .providerCompany("pro1")
                .orderId("new ord1")
                .build();
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.update(1111, providerInvoice))
                .expectNextMatches(updatedProviderInvoice -> {
                    assertEquals(1111, updatedProviderInvoice.getNumber());
                    assertEquals("new ord1", updatedProviderInvoice.getOrderId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testUpdateNonExistingProviderInvoice() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder().number(9999).providerCompany("pro1").build();
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.update(9999, providerInvoice))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testUpdateNotFoundProvider() {
        ProviderInvoice providerInvoice = ProviderInvoice.builder().number(1111).providerCompany("kk").build();
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.update(1111, providerInvoice))
                .expectError(NotFoundException.class)
                .verify();
    }

    @Test
    void testDelete() {
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.delete(3333))
                .verifyComplete();
    }

    @Test
    void testDeleteNotFound() {
        StepVerifier
                .create(this.providerInvoicePersistenceMongodb.delete(9999))
                .expectError(NotFoundException.class)
                .verify();
    }

}