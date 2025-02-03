package br.com.bprates.conta_desafio_dev_api.domain;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@SequenceGenerator(
        name = "account_number_seq_gen",
        sequenceName = "account_number_seq",
        initialValue = 100000,
        allocationSize = 1
)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String agencyNumber;

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_number_seq_gen")
    @Column(unique = true, nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private BigDecimal balance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountBlockStatus blockStatus;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Holder holder;

    public Account() {
    }

    public Account(String agencyNumber, BigDecimal balance, AccountStatus status, AccountBlockStatus blockStatus, Holder holder) {
        this.agencyNumber = agencyNumber;
        this.balance = balance;
        this.status = status;
        this.blockStatus = blockStatus;
        this.holder = holder;
    }

    public Long getId() {
        return id;
    }

    public String getAgencyNumber() {
        return agencyNumber;
    }

    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public void setStatus(AccountStatus status) {
        this.status = status;
    }

    public AccountBlockStatus getBlockStatus() {
        return blockStatus;
    }

    public void setBlockStatus(AccountBlockStatus blockStatus) {
        this.blockStatus = blockStatus;
    }

    public Holder getHolder() {
        return holder;
    }

    public void setHolder(Holder holder) {
        this.holder = holder;
    }
}
