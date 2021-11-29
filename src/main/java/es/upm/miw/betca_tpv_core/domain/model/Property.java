package es.upm.miw.betca_tpv_core.domain.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Property {
    private static Property property;
    private final String miwTpv;

    public Property(@Value("${miw.tpv}")String miwTpv) {
        this.miwTpv = miwTpv;
        property = this;
    }

    public static Property getProperty() {
        return property;
    }

    public String getMiwTpv() {
        return miwTpv;
    }
}
