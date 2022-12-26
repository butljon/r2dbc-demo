package de.butties.r2dbcdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;

import de.butties.r2dbcdemo.domain.Aggregate;
import de.butties.r2dbcdemo.domain.Transaction;
import de.butties.r2dbcdemo.repository.AggregateRepository;
import de.butties.r2dbcdemo.repository.TransactionRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AggregateService {

    private final TransactionRepository transactionRepository;
    private final AggregateRepository aggregateRepository;

    public Mono<Transaction> processTransaction(Transaction transaction) {

        return transactionRepository.findByPeriodAndSequence(transaction.getPeriod(), transaction.getSequence())
                .flatMap(t -> {
                    log.debug("Know transaction {}", t);
                    return Mono.just(t);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Persisting not yet known transaction {}", transaction);
                    return this.processNewTransaction(transaction);
                }));

    }

    private Mono<Transaction> processNewTransaction(Transaction transaction) {

        return transactionRepository.save(transaction)
                .flatMap(t -> upsertAggregate(t))
                .map(a -> {
                    return transaction;
                });

    }

    private Mono<Aggregate> upsertAggregate(Transaction transaction) {

        return aggregateRepository.findByPeriod(transaction.getPeriod())
                .flatMap(a -> {
                    log.debug("Update aggregate {}", a);
                    a.setCount(a.getCount() + 1);
                    return aggregateRepository.save(a);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Creating new aggregate, period {}", transaction.getPeriod());
                    return aggregateRepository.save(Aggregate.builder().period(transaction.getPeriod())
                            .build());
                }));

    }

}
