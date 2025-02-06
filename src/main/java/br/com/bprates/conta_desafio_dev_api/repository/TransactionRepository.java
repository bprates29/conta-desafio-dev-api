package br.com.bprates.conta_desafio_dev_api.repository;

import br.com.bprates.conta_desafio_dev_api.domain.Account;
import br.com.bprates.conta_desafio_dev_api.domain.Transaction;
import br.com.bprates.conta_desafio_dev_api.domain.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByAccountAndTypeAndTimestampBetween(
            Account account, TransactionType transactionType, LocalDateTime startDate, LocalDateTime endDate
    );

    List<Transaction> findAllByAccountAndTimestampBetween(
            Account account, LocalDateTime start, LocalDateTime end
    );
}
