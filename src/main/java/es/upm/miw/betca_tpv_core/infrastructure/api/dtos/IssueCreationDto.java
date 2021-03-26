package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IssueCreationDto {
    @NotBlank
    private String title;
    private String body;
    private String[] labels;
}
