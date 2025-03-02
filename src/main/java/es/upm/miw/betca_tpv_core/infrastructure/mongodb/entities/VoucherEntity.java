package es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities;

import es.upm.miw.betca_tpv_core.domain.model.User;
import es.upm.miw.betca_tpv_core.domain.model.Voucher;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Document
public class VoucherEntity {
    @Id
    private String id;
    @Indexed(unique = true)
    private String reference;
    private BigDecimal value;
    private LocalDateTime creationDate;
    private LocalDateTime dateOfUse;
    @Indexed(unique = true)
    private String userMobile;
    private String userName;

    public VoucherEntity(Voucher voucher) {
        voucher.setReference(UUID.randomUUID().toString());
        BeanUtils.copyProperties(voucher, this, "user");
        if (Objects.nonNull(voucher.getUser())) {
            this.userMobile = voucher.getUser().getMobile();
            this.userName = voucher.getUser().getFirstName();
        }
    }

    public Voucher toVoucher(){
        return Voucher.builder()
                .reference(UUID.randomUUID().toString())
                .value(value)
                .creationDate(creationDate)
                .dateOfUse(dateOfUse)
                .user(Objects.nonNull(this.userMobile)
                        ? User.builder().mobile(this.userMobile).firstName(this.userName).build()
                        : new User())
                .build();
    }
}
