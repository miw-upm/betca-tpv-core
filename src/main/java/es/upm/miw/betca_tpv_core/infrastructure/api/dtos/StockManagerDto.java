package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import es.upm.miw.betca_tpv_core.domain.model.StockManager;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StockManagerDto {
    @NotBlank
    private String barcode;
    private String description;
    private BigDecimal retailPrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateSell;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime dateStockEmpty;
    private Integer stock;

    public StockManagerDto(StockManager stockManager){
        this.barcode = stockManager.getBarcode();
        this.description = stockManager.getDescription();
        this.retailPrice = stockManager.getRetailPrice();
        this.dateSell = stockManager.getDateSell();
        this.dateStockEmpty = stockManager.getDateStockEmpty();
        this.stock = stockManager.getStock();
    }
}
