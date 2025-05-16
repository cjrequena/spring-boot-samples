package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.Account;
import com.cjrequena.sample.domain.Transaction;
import com.cjrequena.sample.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class AccountService {

    private final AccountRepository accountRepository;
    private final RuleService ruleService;


    /**
     * Create a new account.
     */
    public Account create(Account account) {
        log.info("Creating account for customer: {}", account.getCustomerName());
        return accountRepository.save(account);
    }

    /**
     * Retrieve an account by account number.
     */
    public Account retrieveByAccountNumber(String accountNumber) {
        log.debug("Retrieving account by account number: {}", accountNumber);
        return accountRepository.findByAccountNumber(accountNumber);
    }


    /**
     * Process a financial transaction, applying business rules and updating account state.
     *
     * @param transaction The transaction to process
     * @return The processed transaction with status and reason (if applicable)
     */
    @Transactional
    public Transaction process(Transaction transaction) {
        log.info("Processing transaction: type={}, amount={}, accountId={}", transaction.getType(), transaction.getAmount(), transaction.getAccountId());

        Transaction finalTransaction = transaction;
        Account account = accountRepository.findById(transaction.getAccountId())
          .orElseThrow(() -> new IllegalArgumentException("Account not found with ID: " + finalTransaction.getAccountId()));

        // Apply business rules
        transaction = ruleService.applyRules(transaction, account);

        if ("COMPLETED".equalsIgnoreCase(transaction.getStatus())) {
            updateBalance(account, transaction);
            appendTransaction(account, transaction);
            accountRepository.save(account);
            log.info("Transaction completed and account updated. New balance: {}", account.getBalance());
        } else {
            log.warn("Transaction not completed. Status: {}, Reason: {}", transaction.getStatus(), transaction.getReason());
        }

        return transaction;
    }


    // --- Private helpers ---

    private void updateBalance(Account account, Transaction transaction) {
        double balance = account.getBalance();
        if ("DEPOSIT".equalsIgnoreCase(transaction.getType())) {
            account.setBalance(balance + transaction.getAmount());
        } else if ("WITHDRAWAL".equalsIgnoreCase(transaction.getType())) {
            account.setBalance(balance - transaction.getAmount());
        }
    }

    private void appendTransaction(Account account, Transaction transaction) {
        if (account.getTransactions() == null) {
            account.setTransactions(new ArrayList<>());
        }
        account.getTransactions().add(transaction);
    }
}
