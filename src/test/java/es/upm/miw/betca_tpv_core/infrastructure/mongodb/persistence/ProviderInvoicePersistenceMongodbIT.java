package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.TestConfig;
import es.upm.miw.betca_tpv_core.domain.exceptions.ConflictException;
import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

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
                    System.out.println(providerInvoice);
                    assertNotNull(providerInvoice.getNumber());
                    return true;
                })
                .thenCancel()
                .verify();
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
}