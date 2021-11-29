package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleBarcodesDto {
    private List< String > barcodes;
}
