package es.upm.miw.betca_tpv_core.domain.slack;

import es.upm.miw.betca_tpv_core.domain.model.Cashier;
import es.upm.miw.betca_tpv_core.domain.model.SlackMessage;
import reactor.core.publisher.Mono;

public interface SlackMessagePublisher {
    Mono<SlackMessage> create(SlackMessage slackMessage);
    Mono<Cashier> createCloseCashierMessage(Cashier cashier, SlackMessage slackMessage);
}
