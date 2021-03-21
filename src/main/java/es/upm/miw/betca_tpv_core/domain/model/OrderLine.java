package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OrderLine {
    @NotBlank
    private String articleBarcode;
    @NotNull
    private Integer requireAmount;
    private Integer finalAmount;
}