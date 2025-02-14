package es.upm.miw.betca_tpv_core.domain.model;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Property {
    private static String miwTpvStatic;

    private final String miwTpv;

    public Property(@Value("${miw.tpv}") String miwTpv) {
        this.miwTpv = miwTpv;
    }

    public static String getMiwTpv() {
        return miwTpvStatic;
    }

    @PostConstruct
    public void init() {
        Property.miwTpvStatic = this.miwTpv;
    }
}