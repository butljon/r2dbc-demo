package de.butties.r2dbcdemo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

import de.butties.r2dbcdemo.domain.Aggregate;
import de.butties.r2dbcdemo.domain.Transaction;
import de.butties.r2dbcdemo.repository.AggregateRepository;
import de.butties.r2dbcdemo.service.AggregateService;
import de.butties.r2dbcdemo.service.PermutationService;
import de.butties.r2dbcdemo.domain.PermutationRequest;

@Slf4j
@RestController
@RequestMapping("/r2dbc-demo")
@RequiredArgsConstructor
public class RESTController {

    private final AggregateRepository aggregateRepository;
    private final AggregateService aggregateService;
    private final PermutationService permutationService;

    @GetMapping("/aggregates")
    public Flux<Aggregate> getAggregates() {
        log.debug("GET getAggregates");
        return aggregateRepository.findAll();
    }

    @GetMapping("/aggregates/{period}")
    public Mono<Aggregate> getAggregateByPeriod(@PathVariable String period) {
        log.debug("GET getAggregateByPeriod {}", period);
        return aggregateRepository.findByPeriod(Long.parseLong(period));
    }

    @DeleteMapping("/aggregates/{period}")
    public Mono<Integer> deleteAggregate(@PathVariable String period) {
        log.debug("DELETE deleteAggregate {}", period);
        return aggregateRepository.deleteAggregateByPeriod(Long.parseLong(period));
    }
    
    @PostMapping("/transactions/post")
    public Mono<Transaction> processTransaction(@RequestBody Transaction transaction) {
        log.debug("POST processTransaction {}", transaction);
        return aggregateService.processTransaction(transaction);
    }

    @PostMapping("/calculations/permutations")
    public Mono<List<List<Integer>>> processPermutation(@RequestBody PermutationRequest permuationRequest) {
        log.debug("POST calculate permutations {}", permuationRequest);
        return permutationService.calculatePermutation(permuationRequest);
    }

}