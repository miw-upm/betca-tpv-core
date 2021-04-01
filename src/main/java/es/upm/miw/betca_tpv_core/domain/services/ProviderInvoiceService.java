package es.upm.miw.betca_tpv_core.domain.services;

import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.domain.persistence.ProviderInvoicePersistence;
import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoiceTotalTax;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.temporal.TemporalAdjusters;

@Service
public class ProviderInvoiceService {

    private ProviderInvoicePersistence providerInvoicePersistence;

    @Autowired
    public ProviderInvoiceService(ProviderInvoicePersistence providerInvoicePersistence) {
        this.providerInvoicePersistence = providerInvoicePersistence;
    }

    public Flux< ProviderInvoice > findAll() {
        return this.providerInvoicePersistence.findAll();
    }

    public Mono< ProviderInvoice > create(ProviderInvoice providerInvoice) {
        return this.providerInvoicePersistence.create(providerInvoice);
    }

    public Mono< ProviderInvoice > read(Integer number) {
        return this.providerInvoicePersistence.readByNumber(number);
    }

    public Mono< ProviderInvoice > update(Integer number, ProviderInvoice providerInvoice) {
        return this.providerInvoicePersistence.readByNumber(number)
                .map(dataProviderInvoice -> {
                    BeanUtils.copyProperties(providerInvoice, dataProviderInvoice);
                    return dataProviderInvoice;
                })
                .flatMap(dataProviderInvoice -> this.providerInvoicePersistence.update(number, dataProviderInvoice));
    }

    public Mono< Void > delete(Integer number) {
        return this.providerInvoicePersistence.delete(number);
    }

    public Mono< ProviderInvoiceTotalTax > calculateTotalTaxByQuarter(Integer quarter) {
        LocalDate startDate = LocalDate.of(Year.now().getValue(), Month.of(3 * (quarter - 1) + 1), 1);
        LocalDate endDate = LocalDate.from(startDate).plusMonths(2).with(TemporalAdjusters.lastDayOfMonth());
        ProviderInvoiceTotalTax initialProviderInvoiceTotalTax = ProviderInvoiceTotalTax.builder()
                .totalBaseTax(BigDecimal.ZERO)
                .totalTaxValue(BigDecimal.ZERO)
                .build();
        return this.providerInvoicePersistence
                .findByCreationDateBetweenInclusive(startDate, endDate)
                .reduce(initialProviderInvoiceTotalTax, (providerInvoiceTotalTaxAccumulator, providerInvoice) -> {
                    BigDecimal sumBaseTax = providerInvoiceTotalTaxAccumulator.getTotalBaseTax().add(providerInvoice.getBaseTax());
                    providerInvoiceTotalTaxAccumulator.setTotalBaseTax(sumBaseTax);
                    BigDecimal sumTaxValue = providerInvoiceTotalTaxAccumulator.getTotalTaxValue().add(providerInvoice.getTaxValue());
                    providerInvoiceTotalTaxAccumulator.setTotalTaxValue(sumTaxValue);
                    return providerInvoiceTotalTaxAccumulator;
                });
    }
}
