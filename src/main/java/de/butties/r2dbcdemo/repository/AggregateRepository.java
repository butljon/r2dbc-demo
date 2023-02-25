package de.butties.r2dbcdemo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import org.springframework.data.r2dbc.repository.Query;

import de.butties.r2dbcdemo.domain.Aggregate;

@Repository
public interface AggregateRepository extends ReactiveCrudRepository<Aggregate, Integer> {

    public Mono<Aggregate> findByPeriod(Long period);

    @Query("DELETE FROM AGGREGATE WHERE a_id in (select a_id from aggregate where a_period= :period)")
    public Mono<Integer> deleteAggregateByPeriod(Long period);

}