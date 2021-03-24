package es.upm.miw.betca_tpv_core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ArticleLoss {
    @NotBlank
    private String barcode;
    @NotBlank
    private Integer amount;
}
