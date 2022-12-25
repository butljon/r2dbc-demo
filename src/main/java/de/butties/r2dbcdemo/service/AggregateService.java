package de.butties.r2dbcdemo.service;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import org.springframework.stereotype.Service;

import de.butties.r2dbcdemo.domain.Aggregate;
import de.butties.r2dbcdemo.domain.Transaction;
import de.butties.r2dbcdemo.repository.TransactionRepository;

@Service
@RequiredArgsConstructor
public class AggregateService {

    private final TransactionRepository transactionRepository;

    public Mono<Transaction> processTransaction(Transaction transaction) {
    
        return transactionRepository.save(transaction);
    }

}
