package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.ProviderInvoice;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProviderInvoiceEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private Integer number;
    private LocalDateTime creationDate;
    private BigDecimal baseTax;
    private BigDecimal taxValue;
    @DBRef(lazy = true)
    private ProviderEntity providerEntity;
    private String orderId;

    public ProviderInvoice toProviderInvoice() {
        ProviderInvoice providerInvoice = new ProviderInvoice();
        BeanUtils.copyProperties(this, providerInvoice);
        providerInvoice.setProviderCompany(this.getProviderEntity().getCompany());
        return providerInvoice;
    }
}