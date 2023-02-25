package de.butties.r2dbcdemo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;
import java.lang.Thread;
import java.time.temporal.ChronoUnit;

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

    private Map<Long, Long> aggMap = new ConcurrentHashMap<>();

    @Transactional
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

        if(!Objects.isNull(aggMap.get(transaction.getPeriod()))) {
// parameter 100 forces ms interval within which only single DB upsert per period may occur
            long d = 100 - Instant.now().toEpochMilli() + aggMap.get(transaction.getPeriod());
            log.debug("Found period: {}, last: {}, now: {}, delta: {}", transaction.getPeriod(),
                    aggMap.get(transaction.getPeriod()), Instant.now().toEpochMilli(), d);
            if (d > 0) {
                try {
                    log.debug("Period {}, sleep: {}", transaction.getPeriod(), d);
                    Thread.sleep(d);
                } catch (Exception e) {
                    log.debug("Excepption {}", e.getMessage());
                }
                long now = Instant.now().toEpochMilli();
                log.debug("Update period: {} to: {}", transaction.getPeriod(), now);
                aggMap.computeIfPresent(transaction.getPeriod(), (key, oldValue) -> now);
            }
        } else {
            long now = Instant.now().toEpochMilli();
            log.debug("Period not known to cache: {}, insert: {}", transaction.getPeriod(), now);
            aggMap.put(transaction.getPeriod(), now);
        }

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
                    a.increment();
                    return aggregateRepository.save(a);
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.debug("Creating new aggregate, period {}", transaction.getPeriod());
                    return aggregateRepository.save(Aggregate.builder().period(transaction.getPeriod())
                            .build());
                }));

    }

}
