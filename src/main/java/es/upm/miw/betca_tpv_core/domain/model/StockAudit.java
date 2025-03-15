package es.upm.miw.betca_tpv_core.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StockAudit {

    private String id;
    private LocalDateTime creationDate;
    private LocalDateTime closeDate;
    private List<Article> articlesWithoutAudit;
    private Integer lossValue;
    private List<ArticleLoss> losses;

}
