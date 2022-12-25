package de.butties.r2dbcdemo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import de.butties.r2dbcdemo.domain.Aggregate;
import de.butties.r2dbcdemo.domain.Transaction;
import de.butties.r2dbcdemo.repository.AggregateRepository;
import de.butties.r2dbcdemo.service.AggregateService;

@Slf4j
@RestController
@RequestMapping("/r2dbc-demo")
@RequiredArgsConstructor
public class RESTController {

    private final AggregateRepository aggregateRepository;
    private final AggregateService aggregateService;


    @GetMapping("/aggregates")
    public Flux<Aggregate> getAllAggregates() {
        log.debug("GET getAllAggregates");
        return aggregateRepository.findAll();
    }

    @GetMapping("/aggregates/{period}")
    private Flux<Aggregate> getAggregateByPeriod(@PathVariable String period) {
        log.debug("GET getAggregateByPeriod {}", period);
        return aggregateRepository.findByPeriod(Long.parseLong(period));
    }
    
    @PostMapping("/transactions/enter")
    public Mono<Transaction> processTransaction(@RequestBody Transaction transaction) {
        log.debug("POST processTransaction {}", transaction);
        return aggregateService.processTransaction(transaction);
    }

}
