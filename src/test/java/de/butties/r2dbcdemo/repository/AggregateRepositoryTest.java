package de.butties.r2dbcdemo.repository;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.TestInstance;
import static org.junit.jupiter.api.Assertions.assertTrue;
import reactor.test.StepVerifier;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import org.apache.commons.math3.random.RandomDataGenerator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

import de.butties.r2dbcdemo.repository.AggregateRepository;
import de.butties.r2dbcdemo.domain.Aggregate;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AggregateRepositoryTest {

    @Autowired
    private AggregateRepository aggregateRepository;
    
    private final Long leftLimit = 0L;
    private final Long rightLimit = 7L;
    private final RandomDataGenerator rd = new RandomDataGenerator();
    private List<Aggregate> aggregateList = new ArrayList<>();

	@BeforeAll
    public void setUp() {
	
        for(int i=0; i<16; i++) {
            Aggregate aggregate = Aggregate.builder().period(rd.nextLong(leftLimit, rightLimit)).build();
            aggregateList.add(aggregate);
            aggregateRepository.save(aggregate).subscribe();
        }

    }

    @AfterAll
    public void tearDown() {

        aggregateRepository.deleteAll().subscribe();

    }

    @Test
    public void whenSaveAndRetreiveAggregate_thenOK() {

        Long l =rd.nextLong(leftLimit, aggregateList.size());
        int i = l.intValue();

        log.debug("aggregateList size {}, random index {}, aggregate validated {}", aggregateList.size(), i,
                aggregateList.get(i));

        Mono<Aggregate> aggregate = aggregateRepository.findByPeriod(aggregateList.get(i).getPeriod());

        StepVerifier.create(aggregate)
                .assertNext(a -> {
                    log.debug("Retrieved aggregate {}", a);
                    assertTrue(a.getPeriod().equals(aggregateList.get(i).getPeriod()));
                })
                .verifyComplete();
    }

    @Test
    public void whenNoDuplicateAggregates_thenOK() {

        List<Long> aggregateListDistinct = aggregateList.stream()
                .map(a -> a.getPeriod())
                .distinct()
                .collect(Collectors.toList());

        StepVerifier.create(aggregateRepository.findAll())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .consumeRecordedWith(e -> {
                    log.debug("Received aggregate Flux {}", e);
                    log.debug("Validating equal to aggregateListDistinct {}", aggregateListDistinct);
                    assertTrue(e.stream().map(a -> a.getPeriod())
                            .collect(Collectors.toList())
                            .equals(aggregateListDistinct));
                })
                .verifyComplete();

    }

}
