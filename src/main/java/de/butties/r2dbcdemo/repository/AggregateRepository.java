package de.butties.r2dbcdemo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import de.butties.r2dbcdemo.domain.Aggregate;

@Repository
public interface AggregateRepository extends ReactiveCrudRepository<Aggregate, Integer> {
    public Flux<Aggregate> findByPeriod(Long period);
}