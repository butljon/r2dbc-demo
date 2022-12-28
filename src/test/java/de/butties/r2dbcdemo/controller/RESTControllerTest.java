package de.butties.r2dbcdemo.controller;

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

import de.butties.r2dbcdemo.controller.RESTController;
import de.butties.r2dbcdemo.domain.Transaction;
import de.butties.r2dbcdemo.domain.Aggregate;
import de.butties.r2dbcdemo.repository.TransactionRepository;
import de.butties.r2dbcdemo.repository.AggregateRepository;
import de.butties.r2dbcdemo.service.AggregateService;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RESTControllerTest {

    @Autowired
    AggregateRepository aggregateRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    AggregateService aggregateService;// = new AggregateService(transactionRepository, aggregateRepository);
    @Autowired
    RESTController restController;// = new RESTController(aggregateRepository, aggregateService);

    private final Long leftLimit = 0L;
    private final Long rightLimit = 7L;
    private final RandomDataGenerator rd = new RandomDataGenerator();
    private List<Transaction> transactionList = new ArrayList<>();

    @BeforeAll
    public void setup() {

        for(int i=0; i<64; i++) {

            Transaction transaction = Transaction.builder().period(rd.nextLong(leftLimit, rightLimit))
                    .sequence(rd.nextLong(leftLimit, rightLimit)).build();

            transactionList.add(transaction);
            restController.processTransaction(transaction).subscribe();

        }

        for(int i=0; i<4; i++) {

// enter some duplicate transactions
            Long l = rd.nextLong(leftLimit, transactionList.size());
            restController.processTransaction(transactionList.get(l.intValue())).subscribe();

        }

    }

    @AfterAll
    void tearDown() {

        transactionRepository.deleteAll().subscribe();
        aggregateRepository.deleteAll().subscribe();

    }

    @Test
    public void whenCheckAggregateOnePeriod_thenOK() {

        Long l =rd.nextLong(leftLimit, transactionList.size());
        int i = l.intValue();
        long count = transactionList
                .stream()
                .filter(t -> t.getPeriod().equals(transactionList.get(i).getPeriod()))
                .map(t -> t.getPeriod()+"#"+t.getSequence())
                .distinct()
                .count();

        log.debug("Checking aggregate, period {} has count {}", transactionList.get(i).getPeriod(), count);

        Mono<Aggregate> aggregate = restController
                .getAggregateByPeriod(transactionList.get(i).getPeriod().toString());

        StepVerifier.create(aggregate)
                .assertNext(a -> {
                    log.debug("Retrieved aggregate {}", a);
                    assertTrue(a.getPeriod().equals(transactionList.get(i).getPeriod())
                            && a.getCount().equals(count));
                })
                .verifyComplete();
    }

    @Test
    public void whenCheckAllAggregates_thenOK() {

        log.debug("This is current txnList {}", transactionList);
        List<Long> aggregatePeriods = transactionList
                .stream()
                .map(t -> t.getPeriod())
                .distinct()
                .collect(Collectors.toList());

        List<String> aggregateCounts = new ArrayList<>();

        aggregatePeriods.forEach(p ->
            aggregateCounts.add(String.valueOf(p)+"#"+String.valueOf(transactionList
                    .stream()
                    .filter(t -> p.equals(t.getPeriod()))
                    .map(t -> t.getPeriod()+"#"+t.getSequence())
                    .distinct()
                    .count())));

        Flux<Aggregate> aggregates = restController.getAggregates();

        StepVerifier.create(aggregates)
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .consumeRecordedWith(e -> {
                    log.debug("Received aggregate Flux {}", e);
                    log.debug("Validating equal to all aggregateCounts {}", aggregateCounts);
                    assertTrue(e.stream().map(a -> a.getPeriod()+"#"+a.getCount())
                            .collect(Collectors.toList())
                            .equals(aggregateCounts));
                })
                .verifyComplete();

    }

}