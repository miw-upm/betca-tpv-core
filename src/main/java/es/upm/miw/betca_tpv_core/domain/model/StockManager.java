package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class StockManager {
    @NotBlank
    private String barcode;
    private String description;
    private BigDecimal retailPrice;
    private LocalDate dateSell;
    private LocalDate dateStockEmpty;
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

    public static StockManager ofShopping(Shopping article, LocalDate dateCreation) {
        return StockManager.builder()
                .barcode(article.getBarcode())
                .description(article.getDescription())
                .retailPrice(article.getRetailPrice())
                .stock(null)
                .dateSell(dateCreation)
                .dateStockEmpty(null)
                .build();
    }

    public static StockManager ofSoldStock(Article article, int sold) {
        int rest = article.getStock() - sold;
        return StockManager.builder()
                .barcode(article.getBarcode())
                .description(article.getDescription())
                .retailPrice(article.getRetailPrice())
                .stock(rest)
                .dateSell(null)
                .dateStockEmpty(null)
                .build();
    }

    public static StockManager ofEmptyStock(Article article, Integer days) {
        LocalDate dateStockEmpty;
        if(days.equals(-1)){
            dateStockEmpty = null;
        }else if(days < 1){
            dateStockEmpty = LocalDate.now().plusDays(0);
        }else{
            dateStockEmpty = LocalDate.now().plusDays(days);
        }
        return StockManager.builder()
                .barcode(article.getBarcode())
                .description(article.getDescription())
                .retailPrice(article.getRetailPrice())
                .stock(0)
                .dateSell(null)
                .dateStockEmpty(dateStockEmpty)
                .build();
    }
}
