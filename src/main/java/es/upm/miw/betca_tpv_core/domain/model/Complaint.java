package es.upm.miw.betca_tpv_core.domain.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ComplaintCreationDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.ComplaintDto;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Complaint {
    @NotBlank
    private String trackingCode;
    @NotBlank
    private String barcode;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime registrationDate;

    @NotBlank
    private String description;

    private String reply;

    @NotBlank
    private String userMobile;
    @NotBlank
    private ComplaintState state;

    public ComplaintDto toComplaintDto(){
        ComplaintDto complaintDto = new ComplaintDto();
        BeanUtils.copyProperties(this,complaintDto);
        return complaintDto;
    }
}
