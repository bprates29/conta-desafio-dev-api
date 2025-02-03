package br.com.bprates.conta_desafio_dev_api.controller;

import br.com.bprates.conta_desafio_dev_api.domain.Account;
import br.com.bprates.conta_desafio_dev_api.domain.Transaction;
import br.com.bprates.conta_desafio_dev_api.dtos.CreateAccountRequest;
import br.com.bprates.conta_desafio_dev_api.dtos.TransactionRequest;
import br.com.bprates.conta_desafio_dev_api.service.AccountService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody CreateAccountRequest request) {
        Account account = accountService.createAccount(request.cpf());
        return ResponseEntity.ok(account);
    }

    @DeleteMapping("agency/{agency}/account/{account}")
    public ResponseEntity<Void> closeAccount(@PathVariable String agency, @PathVariable String account) {
        accountService.closeAccount(agency, account);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("agency/{agency}/account/{account}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable String agency, @PathVariable String account) {
        accountService.blockAccount(agency, account);
        return ResponseEntity.ok().build();
    }

    @PostMapping("agency/{agency}/account/{account}/unblock")
    public ResponseEntity<Void> unblockAccount(@PathVariable String agency, @PathVariable String account) {
        accountService.unblockAccount(agency, account);
        return ResponseEntity.ok().build();
    }

    @PostMapping("agency/{agency}/account/{account}/deposit")
    public ResponseEntity<Account> deposit(@PathVariable String agency, @PathVariable String account,
                                                  @RequestBody TransactionRequest request) {
        Account updated = accountService.deposit(agency, account, request.amount());
        return ResponseEntity.ok(updated);
    }

    @PostMapping("agency/{agency}/account/{account}/withdraw")
    public ResponseEntity<Account> withdraw(@PathVariable String agency, @PathVariable String account,
                                                   @RequestBody TransactionRequest request) {
        Account updated = accountService.withdraw(agency, account, request.amount());
        return ResponseEntity.ok(updated);
    }

    @GetMapping("agency/{agency}/account/{account}/statement")
    public ResponseEntity<List<Transaction>> getStatement(@PathVariable String agency, @PathVariable String account,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<Transaction> transactions = accountService.getStatement(agency, account, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }
}
