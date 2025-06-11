package es.upm.miw.betca_tpv_core.domain.model;

import lombok.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    private String id;
    @NotNull
    private String userId;
    @NotNull
    private String articleId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer stars;

    private String opinion;
}
