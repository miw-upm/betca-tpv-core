package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockManager {
    @NotBlank
    private String barcode;
    private String description;
    private BigDecimal retailPrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateSell;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateStockEmpty;
    private Integer stock;

    public static StockManager ofProductsByStock(Article article) {
        return StockManager.builder()
                .barcode(article.getBarcode())
                .description(article.getDescription())
                .retailPrice(article.getRetailPrice())
                .stock(article.getStock())
                .dateSell(null)
                .dateStockEmpty(null)
                .build();
    }
}
