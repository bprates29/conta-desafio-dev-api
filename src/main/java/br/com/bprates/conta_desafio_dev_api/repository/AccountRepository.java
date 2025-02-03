package br.com.bprates.conta_desafio_dev_api.repository;

import br.com.bprates.conta_desafio_dev_api.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAgencyNumberAndAccountNumber(String agencyNumber, String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
}
