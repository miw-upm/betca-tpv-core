package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)

public class Voucher {
    @NotBlank
    private String reference;
    @PositiveBigDecimal
    private BigDecimal value;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateOfUse;
    @NotNull
    private User user;

    public void doDefault(){
        this.reference = UUIDBase64.URL.encode();
        if (Objects.isNull(creationDate)) {
            this.creationDate = LocalDateTime.now();
        }
        if (Objects.isNull(value)) {
            this.value = BigDecimal.valueOf(60);
        }
        if (Objects.isNull(user)) {
            this.user = User.builder().mobile("666666000").build();
        }
    }
}
