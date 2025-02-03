package br.com.bprates.conta_desafio_dev_api.repository;

import br.com.bprates.conta_desafio_dev_api.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Transaction findByTransactionNumber(String transactionNumber);
    boolean existsByTransactionNumber(String transactionNumber);
}
