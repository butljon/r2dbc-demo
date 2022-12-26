package de.butties.r2dbcdemo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import de.butties.r2dbcdemo.domain.Aggregate;

@Repository
public interface AggregateRepository extends ReactiveCrudRepository<Aggregate, Integer> {

    public Mono<Aggregate> findByPeriod(Long period);

}