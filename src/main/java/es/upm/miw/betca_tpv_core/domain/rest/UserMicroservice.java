package es.upm.miw.betca_tpv_core.domain.rest;

import es.upm.miw.betca_tpv_core.domain.model.User;
import reactor.core.publisher.Mono;

public interface UserMicroservice {

    Mono< User > readByMobile(String mobile);
}
