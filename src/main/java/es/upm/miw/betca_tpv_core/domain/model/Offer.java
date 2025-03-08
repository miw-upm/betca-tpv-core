package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.domain.services.utils.UUIDBase64;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Offer {
    private String reference;
    @NotBlank
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiryDate;
    @NotNull
    @Min(0)
    @Max(100)
    private Integer discount;
    private List<Article> articleList;

    public void doDefault() {
        this.reference = UUIDBase64.URL.encode();
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
            this.discount = 10;
        }
        if (Objects.isNull(articleList)) {
            this.articleList = new ArrayList<>();
        }
    }
}
