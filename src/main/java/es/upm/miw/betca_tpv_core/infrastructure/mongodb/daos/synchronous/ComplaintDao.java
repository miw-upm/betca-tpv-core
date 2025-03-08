package es.upm.miw.betca_tpv_core.infrastructure.mongodb.daos.synchronous;


import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ArticleEntity;
import es.upm.miw.betca_tpv_core.infrastructure.mongodb.entities.ComplaintEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ComplaintDao extends MongoRepository<ComplaintEntity,String> {
    List<ComplaintEntity> findByArticle_Barcode(String barcode);

    List<ComplaintEntity> findByUserMobile(String userMobile);
}
