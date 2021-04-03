package es.upm.miw.betca_tpv_core.infrastructure.api.dtos;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.model.RgpdType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RgpdUserDto {
    private String mobile;
    private RgpdType rgpdType;

    public static RgpdUserDto ofRgpd(Rgpd rgpd) {
        return new RgpdUserDto(rgpd.getMobile(), rgpd.getRgpdType());
    }

    public static RgpdUserDto ofString(String string) {
        RgpdUserDto rgpdUserDto = new RgpdUserDto();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            rgpdUserDto = objectMapper.readValue(string, RgpdUserDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return rgpdUserDto;
    }

}
