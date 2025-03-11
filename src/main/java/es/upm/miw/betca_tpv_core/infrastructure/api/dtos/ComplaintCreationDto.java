package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComplaintCreationDto {

    @NotBlank
    @NotNull
    private String barcode;
    @NotBlank
    @NotNull
    private String description;






}
