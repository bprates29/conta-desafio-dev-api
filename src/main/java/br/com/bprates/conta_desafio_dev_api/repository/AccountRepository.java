package br.com.bprates.conta_desafio_dev_api.repository;

import br.com.bprates.conta_desafio_dev_api.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByAgencyNumberAndAccountNumber(String agencyNumber, String accountNumber);
    @Query("SELECT COALESCE(MAX(CAST(a.accountNumber AS long)), 100000) FROM Account a")
    Long findMaxAccountNumber();

}
