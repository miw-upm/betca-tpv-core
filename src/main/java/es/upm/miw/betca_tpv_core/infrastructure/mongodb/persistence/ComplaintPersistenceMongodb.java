package es.upm.miw.betca_tpv_core.infrastructure.mongodb.persistence;

import es.upm.miw.betca_tpv_core.domain.exceptions.NotFoundException;
import es.upm.miw.betca_tpv_core.domain.model.Complaint;
import es.upm.miw.betca_tpv_core.domain.persistence.ComplaintPersistence;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ArticleReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.ComplaintReactive;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintEntity;
import es.upm.miw.betca_tpv_core.domain.model.ComplaintState;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Log4j2
@Repository
public class ComplaintPersistenceMongodb implements ComplaintPersistence {

    private final ComplaintReactive complaintReactive;

    private final ArticleReactive articleReactive;

    @Autowired
    public ComplaintPersistenceMongodb(ComplaintReactive complaintReactive,ArticleReactive articleReactive){
        this.complaintReactive=complaintReactive;
        this.articleReactive=articleReactive;
    }

    @Override
    public Mono<Complaint> create(Complaint complaint) {
        ComplaintEntity complaintEntity = new ComplaintEntity();
        BeanUtils.copyProperties(complaint,complaintEntity);

        return this.articleReactive.findByBarcode(complaint.getBarcode())
                .switchIfEmpty(Mono.error(new NotFoundException("The article does not exist with the barcode provided.")))
                .map(articleEntity -> {
                    complaintEntity.setArticle(articleEntity);
                    complaintEntity.setState(ComplaintState.OPEN);
                    return complaintEntity;
                })
                .flatMap(this.complaintReactive::save)
                .map(ComplaintEntity::toComplaint);
    }

    @Override
    public Flux<Complaint> findByUserMobileNullSafe(String userMobile) {
        return complaintReactive.findByUserMobileNullSafe(userMobile).map(ComplaintEntity::toComplaint);
    }

    @Override
    public Mono<Complaint> readByTrackingCode(String trackingCode) {
        return complaintReactive.findByTrackingCode(trackingCode)
                .map(ComplaintEntity::toComplaint);
    }

    @Override
    public Mono<Complaint> findByUserMobileAndBarcodeAndState(String userMobile, String barcode,ComplaintState state) {
        return  this.articleReactive.findByBarcode(barcode)
                .switchIfEmpty(Mono.empty())
                .flatMap(articleEntity -> {
                    return this.complaintReactive.findByUserMobileAndArticleAndState(userMobile,articleEntity,state)
                            .map(ComplaintEntity::toComplaint);
                });

    }

    @Override
    public Mono<Void> delete(Complaint complaint) {
        return this.complaintReactive.findByTrackingCode(complaint.getTrackingCode())
                .flatMap(this.complaintReactive::delete);
    }
}
