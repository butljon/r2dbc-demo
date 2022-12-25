package de.butties.r2dbcdemo.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import de.butties.r2dbcdemo.domain.Transaction;

@Repository
public interface TransactionRepository extends ReactiveCrudRepository<Transaction, Integer> {

}