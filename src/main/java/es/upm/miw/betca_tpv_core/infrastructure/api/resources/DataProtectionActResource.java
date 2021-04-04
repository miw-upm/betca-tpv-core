package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.model.Rgpd;
import es.upm.miw.betca_tpv_core.domain.services.DataProtectionActService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.RgpdUserDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.resources.utils.FileConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.io.File;

@Rest
@RequestMapping(DataProtectionActResource.DATA_PROTECTION_ACT)
public class DataProtectionActResource {

    public static final String DATA_PROTECTION_ACT = "/data-protection-act";
    public static final String AGREEMENT_ID = "/agreement";
    public static final String MOBILE_ID = "/{mobile}";
    public static final String FILE_PATH = "/tpv-pdfs/agreements/";
    public static final String AGREEMENT = "agreement";
    public static final String USER = "user";
    public static final String FILE_EXTENSION = ".pdf";

    private DataProtectionActService dataProtectionActService;

    @Autowired
    public DataProtectionActResource(DataProtectionActService dataProtectionActService) {
        this.dataProtectionActService = dataProtectionActService;
    }

    @GetMapping(DataProtectionActResource.MOBILE_ID)
    public Mono<RgpdUserDto> read(@PathVariable String mobile) {
        return this.dataProtectionActService.read(mobile)
                .map(RgpdUserDto::ofRgpd);
    }

    @PreAuthorize("permitAll()")
    @PostMapping(produces = {"application/json"})
    public Mono<RgpdUserDto> create(@RequestPart(USER) String user,
                                    @RequestPart(AGREEMENT) FilePart agreement) {
        RgpdUserDto rgpdUserDto = RgpdUserDto.ofString(user);
        File file = new FileConverter(FILE_PATH, AGREEMENT + "-" + rgpdUserDto.getMobile(), FILE_EXTENSION).convert(agreement);
        return this.dataProtectionActService.create(
                new Rgpd(
                        rgpdUserDto.getMobile(),
                        rgpdUserDto.getRgpdType(),
                        FileConverter.getBytes(file)
                )
        ).map(RgpdUserDto::ofRgpd);
    }

    @PutMapping(DataProtectionActResource.MOBILE_ID)
    public Mono<RgpdUserDto> update(@PathVariable String mobile,
                                    @RequestPart(USER) String user,
                                    @RequestPart(AGREEMENT) FilePart agreement) {
        RgpdUserDto rgpdUserDto = RgpdUserDto.ofString(user);
        File file = new FileConverter(FILE_PATH, AGREEMENT + "-" + rgpdUserDto.getMobile(), FILE_EXTENSION).convert(agreement);
        return this.dataProtectionActService.update(
                mobile,
                new Rgpd(
                        rgpdUserDto.getMobile(),
                        rgpdUserDto.getRgpdType(),
                        FileConverter.getBytes(file)
                )
        ).map(RgpdUserDto::ofRgpd);
    }

    @GetMapping(value = DataProtectionActResource.AGREEMENT_ID + DataProtectionActResource.MOBILE_ID,
            produces = {"application/pdf", "application/json"})
    public Mono<byte[]> readAgreement(@PathVariable String mobile) {
        return this.dataProtectionActService.read(mobile)
                .map(Rgpd::getAgreement);
    }

    @PreAuthorize("permitAll()")
    @GetMapping(value = DataProtectionActResource.AGREEMENT_ID, produces = {"application/pdf", "application/json"})
    public Mono<byte[]> readUnsignedAgreement(@RequestParam("mobile") String mobile, @RequestParam("rgpdType") String rgpdType) {
        return this.dataProtectionActService
                .createAgreement(new Rgpd(mobile, rgpdType));
    }

}
