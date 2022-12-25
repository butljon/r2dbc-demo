package de.butties.r2dbcdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;

import de.butties.r2dbcdemo.domain.Aggregate;
import de.butties.r2dbcdemo.domain.Transaction;
import de.butties.r2dbcdemo.repository.TransactionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregateService {

    private final TransactionRepository transactionRepository;

    public Mono<Transaction> processTransaction(Transaction transaction) {

        return transactionRepository.findByPeriodAndSequence(transaction.getPeriod(), transaction.getSequence())
                .flatMap(t -> {
                    log.debug("Know transaction {}", t);
                    return Mono.just(t);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Persisting not yet known transaction {}", transaction);
                    return transactionRepository.save(transaction);
                }));

    }

}
