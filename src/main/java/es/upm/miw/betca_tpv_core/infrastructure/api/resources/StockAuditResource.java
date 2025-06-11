package es.upm.miw.betca_tpv_core.infrastructure.api.resources;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.StockAudit;
import es.upm.miw.betca_tpv_core.domain.services.StockAuditService;
import es.upm.miw.betca_tpv_core.infrastructure.api.Rest;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditCreateDto;
import es.upm.miw.betca_tpv_core.infrastructure.api.dtos.StockAuditUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@Rest
@RestController
@RequestMapping(StockAuditResource.STOCK_AUDIT)
public class StockAuditResource {

    public static final String STOCK_AUDIT = "/stock-audits";
    public static final String STOCK_AUDIT_ID = "/{id}";
    public static final String STOCK_AUDIT_CLOSE = "/close";

    private final StockAuditService stockAuditService;

    @Autowired
    public StockAuditResource(StockAuditService stockAuditService) {
        this.stockAuditService = stockAuditService;
    }

    @GetMapping
    public Flux<StockAudit> readAll() {  // Cambiado de findAll a readAll
        return stockAuditService.readAll();
    }

    @GetMapping(STOCK_AUDIT_ID)
    public Mono<StockAudit> readOne(@PathVariable String id) {  // Cambiado de read a readOne
        return stockAuditService.readOne(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<StockAuditCreateDto> create() {
        return this.stockAuditService.create();
    }

    //@PutMapping(STOCK_AUDIT_ID + STOCK_AUDIT_CLOSE)
    @PutMapping("/{id}/close")
    public Mono<Void> close(@PathVariable String id) {
        return this.stockAuditService.close(id)
                .onErrorMap(IllegalStateException.class,
                        e -> new ResponseStatusException(
                                HttpStatus.PRECONDITION_FAILED,
                                "No se puede cerrar la auditoría: hay artículos pendientes de auditar."
                        ))
                .onErrorMap(NotFoundException.class,
                        e -> new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "No se encontró la auditoría de stock con el ID proporcionado."
                        ));
    }

    @PutMapping(STOCK_AUDIT_ID)
    public Mono<Void> update(@PathVariable String id, @RequestBody StockAuditUpdateDto updateDto) {
        return this.stockAuditService.update(id, updateDto)
                .onErrorMap(IllegalArgumentException.class,
                        e -> new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage()))
                .onErrorMap(NotFoundException.class,
                        e -> new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage()));
    }
}