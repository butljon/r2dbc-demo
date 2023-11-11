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
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.stream.Collectors;


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
    public Flux<Aggregate> getAggregates() {

        List<List<Integer>> result = subsets(new ArrayList<>(), Arrays.asList(0, 1, 2, 3, 5, 6, 4, 7), 3);
        for(List<Integer> val : result) {
            log.debug("GET getAggregates subsets {}", val);
        }

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

    static List<List<Integer>> subsets(List<Integer> root, List<Integer> in, int m) {
        if (m < 1) {
            return Collections.singletonList(root);
        }
        return in.stream()
                .flatMap(e -> subsets(add(root, e), remove(in, e), m-1).stream()).collect(Collectors.toList());
    }

    static List<Integer> add(List<Integer> in, Integer e) {
        List<Integer> out = new ArrayList<>(in);
        out.add(e);
        return out;
    }

    static List<Integer> remove(List<Integer> in, Integer e) {
        return in.stream().filter(l -> l >= e).collect(Collectors.toList());
    }

}