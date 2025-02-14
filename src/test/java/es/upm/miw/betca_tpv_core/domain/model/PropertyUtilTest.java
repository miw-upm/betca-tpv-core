package es.upm.miw.betca_tpv_core.domain.model;

import es.upm.miw.betca_tpv_core.TestConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestConfig
class PropertyUtilTest {

    @Test
    void testGetMiwTpv() {
        assertEquals("http://localhost:4200", Property.getMiwTpv());
    }

}
