package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor

@Getter
@Setter

public class StockAudit {
    private String id;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime closeDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updateDate;

    private List<ArticleAudit> articlesWithoutAudit; // Artículos pendientes (real=null)
    private BigDecimal lossValue;                   // Pérdida monetaria total
    private List<ArticleLoss> losses;              // {barcode, amount}
    private List<ArticleAudit> articlesAudited;    // Artículos con real!=null
}