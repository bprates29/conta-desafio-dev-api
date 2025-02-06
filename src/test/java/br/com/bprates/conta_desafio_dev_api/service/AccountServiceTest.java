package br.com.bprates.conta_desafio_dev_api.service;

import br.com.bprates.conta_desafio_dev_api.domain.*;
import br.com.bprates.conta_desafio_dev_api.repository.AccountRepository;
import br.com.bprates.conta_desafio_dev_api.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    public static final String AGENCY_NUMBER = "0001";
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private HolderService holderService;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAccount_success() {
        String cpf = "12345678900";
        Holder holder = new Holder();
        holder.setCpf(cpf);
        when(holderService.findByCpf(cpf)).thenReturn(holder);
        when(accountRepository.findMaxAccountNumber()).thenReturn(null);
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account account = accountService.createAccount(cpf);

        assertNotNull(account);
        assertEquals("0001", account.getAgencyNumber());
        assertEquals("100000", account.getAccountNumber());
        assertEquals(BigDecimal.ZERO, account.getBalance());
        assertEquals(AccountStatus.ACTIVE, account.getStatus());
        assertEquals(AccountBlockStatus.UNBLOCKED, account.getBlockStatus());
        assertEquals(holder, account.getHolder());
    }

    @Test
    void createAccount_holderNotFound() {
        String cpf = "12345678900";
        when(holderService.findByCpf(cpf)).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.createAccount(cpf));
        assertEquals("Holder not found, CPF: 12345678900", exception.getMessage());
    }

    @Test
    void deposit_success() {
        String accountNumber = "100000";
        BigDecimal amount = new BigDecimal("1000");
        Account account = new Account();
        account.setAgencyNumber(AGENCY_NUMBER);
        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setBlockStatus(AccountBlockStatus.UNBLOCKED);

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Account updatedAccount = accountService.deposit(AGENCY_NUMBER, accountNumber, amount);

        var expectedAmount = new BigDecimal("1000");
        assertNotNull(updatedAccount);
        assertEquals(expectedAmount, updatedAccount.getBalance());
    }

    @Test
    void deposit_negativeAmount() {
        String accountNumber = "100000";
        BigDecimal amount = new BigDecimal("-1000");
        Account account = nextAccount(AGENCY_NUMBER, accountNumber);

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.deposit(AGENCY_NUMBER, accountNumber, amount));
        assertEquals("Amount must be positive", exception.getMessage());
    }

    private static Account nextAccount(String agencyNumber, String accountNumber) {
        Account account = new Account();
        account.setAgencyNumber(agencyNumber);
        account.setAccountNumber(accountNumber);
        account.setBalance(new BigDecimal("2000"));
        account.setStatus(AccountStatus.ACTIVE);
        account.setBlockStatus(AccountBlockStatus.UNBLOCKED);
        return account;
    }

    @Test
    void withdraw_success() {
        String accountNumber = "100000";
        BigDecimal amount = new BigDecimal("1000");
        Account account = nextAccount(AGENCY_NUMBER, accountNumber);

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(transactionRepository.findAllByAccountAndTypeAndTimestampBetween(any(Account.class), eq(TransactionType.WITHDRAW), any(), any())).thenReturn(List.of());

        Account updatedAccount = accountService.withdraw(AGENCY_NUMBER, accountNumber, amount);

        var expectedAmount = new BigDecimal("1000");
        assertNotNull(updatedAccount);
        assertEquals(expectedAmount, updatedAccount.getBalance());
    }

    @Test
    void withdraw_insufficientBalance() {
        String accountNumber = "100000";
        BigDecimal amount = new BigDecimal("3000");
        Account account = nextAccount(AGENCY_NUMBER, accountNumber);

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.withdraw(AGENCY_NUMBER, accountNumber, amount));
        assertEquals("Insufficient balance for withdrawal", exception.getMessage());
    }

    @Test
    void withdraw_dailyLimitExceeded() {
        String accountNumber = "100000";
        BigDecimal amount = new BigDecimal("3000");
        Account account = new Account();
        account.setAgencyNumber(AGENCY_NUMBER);
        account.setAccountNumber(accountNumber);
        account.setBalance(new BigDecimal("5000"));
        account.setStatus(AccountStatus.ACTIVE);
        account.setBlockStatus(AccountBlockStatus.UNBLOCKED);

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.of(account));
        when(transactionRepository.findAllByAccountAndTypeAndTimestampBetween(any(Account.class), eq(TransactionType.WITHDRAW), any(), any())).thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.withdraw(AGENCY_NUMBER, accountNumber, amount));
        assertEquals("Daily withdrawal limit exceeded. Remaining today: 2000", exception.getMessage());
    }

    @Test
    void deposit_accountBlocked() {
        String accountNumber = "100000";
        BigDecimal amount = new BigDecimal("1000");
        Account account = new Account();
        account.setAgencyNumber(AGENCY_NUMBER);
        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.ACTIVE);
        account.setBlockStatus(AccountBlockStatus.BLOCKED);

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.deposit(AGENCY_NUMBER, accountNumber, amount));
        assertEquals("Account is blocked", exception.getMessage());
    }

    @Test
    void deposit_accountInactive() {
        String accountNumber = "100000";
        BigDecimal amount = new BigDecimal("1000");
        Account account = new Account();
        account.setAgencyNumber(AGENCY_NUMBER);
        account.setAccountNumber(accountNumber);
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(AccountStatus.CLOSED);
        account.setBlockStatus(AccountBlockStatus.UNBLOCKED);

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.deposit(AGENCY_NUMBER, accountNumber, amount));
        assertEquals("Account is not active", exception.getMessage());
    }

    @Test
    void withdraw_accountNotFound() {
        String accountNumber = "999999";
        BigDecimal amount = new BigDecimal("1000");

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.withdraw(AGENCY_NUMBER, accountNumber, amount));
        assertEquals("Account not found: 999999", exception.getMessage());
    }

    @Test
    void withdraw_accountBlocked() {
        String accountNumber = "100000";
        BigDecimal amount = new BigDecimal("1000");
        Account account = new Account();
        account.setAgencyNumber(AGENCY_NUMBER);
        account.setAccountNumber(accountNumber);
        account.setBalance(new BigDecimal("2000"));
        account.setStatus(AccountStatus.ACTIVE);
        account.setBlockStatus(AccountBlockStatus.BLOCKED);

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.withdraw(AGENCY_NUMBER, accountNumber, amount));
        assertEquals("Account is blocked", exception.getMessage());
    }

    @Test
    void withdraw_accountInactive() {
        String accountNumber = "100000";
        BigDecimal amount = new BigDecimal("1000");
        Account account = new Account();
        account.setAgencyNumber(AGENCY_NUMBER);
        account.setAccountNumber(accountNumber);
        account.setBalance(new BigDecimal("2000"));
        account.setStatus(AccountStatus.CLOSED);
        account.setBlockStatus(AccountBlockStatus.UNBLOCKED);

        when(accountRepository.findByAgencyNumberAndAccountNumber(AGENCY_NUMBER, accountNumber)).thenReturn(Optional.of(account));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> accountService.withdraw(AGENCY_NUMBER, accountNumber, amount));
        assertEquals("Account is not active", exception.getMessage());
    }
}