package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Complaint {

    @NotBlank
    private String barcode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") // pattern="dd/MM/yyyy hh:mm" o iso = DateTimeFormat.ISO.TIME
    private LocalDateTime registrationDate;

    @NotBlank
    private String description;

    private String reply;

    @NotBlank
    private String mobile;

    @Enumerated(EnumType.STRING)
    private ComplaintState state;

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
