package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class VoucherEntity {

    private String reference;
    private Integer value;
    private LocalDateTime creationDate;
    private LocalDateTime dateOfUse;

    public Voucher toVoucher() {
        Voucher voucher = new Voucher();
        BeanUtils.copyProperties(this, voucher);
        voucher.setReference(UUID.fromString(this.getReference()));
        return voucher;
    }
}
