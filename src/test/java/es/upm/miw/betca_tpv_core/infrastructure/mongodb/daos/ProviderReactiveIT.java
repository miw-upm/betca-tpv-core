package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.test.StepVerifier;

@TestConfig
class ProviderReactiveIT {

    @Autowired
    private ProviderReactive providerReactive;

    @Test
    void testFindByCompanyNullSafe() {
        StepVerifier
                .create(this.providerReactive.findByCompany(null))
                .expectComplete()
                .verify();
    }

}
