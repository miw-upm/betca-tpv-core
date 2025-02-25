package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.model.validations.PositiveBigDecimal;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer {
    @NotBlank
    private String reference;
    @NotBlank
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
    @PositiveBigDecimal
    private BigDecimal discount;
    private List<Article> articleList;

    public void doDefault() {
        if (Objects.isNull(reference)) {
            this.reference = UUID.randomUUID().toString();
        }
        if (Objects.isNull(creationDate)) {
            this.creationDate = LocalDateTime.now();
        }
        if (Objects.isNull(expiryDate)) {
            this.expiryDate = LocalDateTime.now().plusDays(30);
        }
        if (Objects.isNull(description)) {
            this.description = "Default offer description";
        }
        if (Objects.isNull(discount)) {
            this.discount = BigDecimal.valueOf(10);
        }
        if (Objects.isNull(articleList)) {
            this.articleList = new ArrayList<>();
        }
    }
}
