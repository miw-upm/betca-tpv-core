package es.upm.miw.betca_tpv_core.domain.model;

import java.math.BigDecimal;

public enum Tax {
    FREE("0"), SUPER_REDUCED("4"), REDUCED("10"), GENERAL("21");

    private final BigDecimal rate;

    Tax(String tax) {
        this.rate = new BigDecimal(tax);
    }

    public BigDecimal getRate() {
        return rate;
    }

    @Override
    public String toString() {
        return this.name() + "(" + this.rate + ")";
    }
}
