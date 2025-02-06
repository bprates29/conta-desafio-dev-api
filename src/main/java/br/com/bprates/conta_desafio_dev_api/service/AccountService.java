package br.com.bprates.conta_desafio_dev_api.service;

import br.com.bprates.conta_desafio_dev_api.domain.Account;
import br.com.bprates.conta_desafio_dev_api.domain.AccountBlockStatus;
import br.com.bprates.conta_desafio_dev_api.domain.AccountStatus;
import br.com.bprates.conta_desafio_dev_api.domain.Holder;
import br.com.bprates.conta_desafio_dev_api.domain.Transaction;
import br.com.bprates.conta_desafio_dev_api.domain.TransactionType;
import br.com.bprates.conta_desafio_dev_api.repository.AccountRepository;
import br.com.bprates.conta_desafio_dev_api.repository.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final HolderService holderService;

    private static final BigDecimal DAILY_WITHDRAW_LIMIT = new BigDecimal("2000");
    private static final String AGENCY_NUMBER_PATTERS = "0001";

    public AccountService(AccountRepository accountRepository,
                                 TransactionRepository transactionRepository,
                                 HolderService holderService) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.holderService = holderService;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    @Transactional
    public Account createAccount(String cpf) {
        Holder holder = holderService.findByCpf(cpf);
        if (holder == null) {
            throw new RuntimeException("Holder not found, CPF: " + cpf);
        }

        Long maxAccountNumber = accountRepository.findMaxAccountNumber();
        if (maxAccountNumber == null) {
            maxAccountNumber = 100000L;
        } else {
            maxAccountNumber++;
        }

        Account account = new Account(
                AGENCY_NUMBER_PATTERS,
                maxAccountNumber.toString(),
                BigDecimal.ZERO,
                AccountStatus.ACTIVE,
                AccountBlockStatus.UNBLOCKED,
                holder
        );

        return accountRepository.save(account);
    }

    @Transactional
    public void closeAccount(String agencyNumber, String accountNumber) {
        Account account = getAccountOrThrow(agencyNumber, accountNumber);
        account.setStatus(AccountStatus.CLOSED);
        accountRepository.save(account);
    }

    @Transactional
    public void blockAccount(String agencyNumber, String accountNumber) {
        Account account = getAccountOrThrow(agencyNumber, accountNumber);
        account.setBlockStatus(AccountBlockStatus.BLOCKED);
        accountRepository.save(account);
    }

    @Transactional
    public void unblockAccount(String agencyNumber, String accountNumber) {
        Account account = getAccountOrThrow(agencyNumber, accountNumber);
        account.setBlockStatus(AccountBlockStatus.UNBLOCKED);
        accountRepository.save(account);
    }

    @Transactional
    public Account deposit(String agencyNumber, String accountNumber, BigDecimal amount) {
        Account account = getAccountOrThrow(agencyNumber, accountNumber);

        validateAccountActiveAndUnblocked(account);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be positive");
        }

        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);

        accountRepository.save(account);

        Transaction transaction = new Transaction(TransactionType.DEPOSIT, amount, account);
        transactionRepository.save(transaction);

        return account;
    }

    @Transactional
    public Account withdraw(String agencyNumber, String accountNumber, BigDecimal amount) {
        Account account = getAccountOrThrow(agencyNumber, accountNumber);

        validateAccountActiveAndUnblocked(account);

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Withdrawal amount must be positive");
        }

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance for withdrawal");
        }

        BigDecimal dailyWithdrawn = getTotalWithdrawByDay(account, LocalDate.now());
        BigDecimal limitRemaining = DAILY_WITHDRAW_LIMIT.subtract(dailyWithdrawn);
        if (amount.compareTo(limitRemaining) > 0) {
            throw new RuntimeException("Daily withdrawal limit exceeded. Remaining today: " + limitRemaining);
        }

        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);

        accountRepository.save(account);

        Transaction transaction = new Transaction(TransactionType.WITHDRAW, amount, account);
        transactionRepository.save(transaction);

        return account;
    }

    public List<Transaction> getStatement(String agencyNumber, String accountNumber, LocalDate startDate, LocalDate endDate) {
        Account account = getAccountOrThrow(agencyNumber, accountNumber);
        return transactionRepository.findAllByAccountAndTimestampBetween(account, startDate.atStartOfDay(), endDate.atTime(23, 59, 59));
    }

    private Account getAccountOrThrow(String agencyNumber, String accountNumber) {
        return accountRepository.findByAgencyNumberAndAccountNumber(agencyNumber, accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
    }

    private void validateAccountActiveAndUnblocked(Account account) {
        if (!account.getStatus().equals(AccountStatus.ACTIVE)) {
            throw new RuntimeException("Account is not active");
        }
        if (account.getBlockStatus().equals(AccountBlockStatus.BLOCKED)) {
            throw new RuntimeException("Account is blocked");
        }
    }

    private BigDecimal getTotalWithdrawByDay(Account account, LocalDate date) {
        BigDecimal total = BigDecimal.ZERO;
        List<Transaction> transactions = transactionRepository.findAllByAccountAndTypeAndTimestampBetween(account, TransactionType.WITHDRAW, date.atStartOfDay(), date.atTime(23, 59, 59));
        for (Transaction t : transactions) {
            if (t.getAccount().getId().equals(account.getId())
                    && t.getType() == TransactionType.WITHDRAW
                    && t.getTimestamp().toLocalDate().equals(date)) {
                total = total.add(t.getAmount());
            }
        }
        return total;
    }

}
