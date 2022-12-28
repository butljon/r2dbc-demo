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

import de.butties.r2dbcdemo.repository.TransactionRepository;
import de.butties.r2dbcdemo.domain.Transaction;

@Slf4j
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;
    
    private final Long leftLimit = 0L;
    private final Long rightLimit = 7L;
    private final RandomDataGenerator rd = new RandomDataGenerator();
    private List<Transaction> transactionList = new ArrayList<>();

	@BeforeAll
    public void setUp() {
	
        for(int i=0; i<128; i++) {
            Transaction transaction = Transaction.builder().period(rd.nextLong(leftLimit, rightLimit))
                    .sequence(rd.nextLong(leftLimit, rightLimit)).build();
            transactionList.add(transaction);
            transactionRepository.save(transaction).subscribe();
        }

    }

    @AfterAll
    public void tearDown() {

        transactionRepository.deleteAll().subscribe();

    }

    @Test
    public void whenSaveAndRetreiveTransaction_thenOK() {

        Long l =rd.nextLong(leftLimit, transactionList.size());
        int i = l.intValue();

        log.debug("transactionList size {}, random index {}, transaction validated {}", transactionList.size(), i,
                transactionList.get(i));

        Mono<Transaction> transaction = transactionRepository
                .findByPeriodAndSequence(transactionList.get(i).getPeriod(), transactionList.get(i).getSequence());

        StepVerifier.create(transaction)
                .assertNext(t -> {
                    log.debug("Retrieved transaction {}", t);
                    assertTrue(t.getPeriod().equals(transactionList.get(i).getPeriod())
                            && t.getSequence().equals(transactionList.get(i).getSequence()));
                })
                .verifyComplete();
    }

    @Test
    public void whenNoDuplicateTransactions_thenOK() {

        List<String> transactionListDistinct = transactionList.stream()
                .map(a -> a.getPeriod().toString()+"#"+a.getSequence().toString())
                .distinct()
                .collect(Collectors.toList());

        StepVerifier.create(transactionRepository.findAll())
                .recordWith(ArrayList::new)
                .thenConsumeWhile(x -> true)
                .consumeRecordedWith(e -> {
                    log.debug("Received transaction Flux {}", e);
                    log.debug("Validating equal to transactionListDistinct {}", transactionListDistinct);
                    assertTrue(e.stream().map(a -> a.getPeriod().toString()+"#"+a.getSequence().toString())
                            .collect(Collectors.toList())
                            .equals(transactionListDistinct));
                })
                .verifyComplete();
    }

}
