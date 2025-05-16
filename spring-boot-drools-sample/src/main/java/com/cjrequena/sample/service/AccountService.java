package com.cjrequena.sample.service;


import com.cjrequena.sample.domain.Account;
import com.cjrequena.sample.domain.Transaction;
import com.cjrequena.sample.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private RulesService rulesService;
    
    @Transactional
    public Transaction process(Transaction transaction) {
        // Apply business rules
        transaction = rulesService.applyRules(transaction);
        
        // If transaction is approved, update account balance
        if ("COMPLETED".equals(transaction.getStatus())) {
            Account account = transaction.getAccount();
            if ("DEPOSIT".equals(transaction.getType())) {
                account.setBalance(account.getBalance() + transaction.getAmount());
            } else if ("WITHDRAWAL".equals(transaction.getType())) {
                account.setBalance(account.getBalance() - transaction.getAmount());
            }

            if (account.getTransactions() == null) {
                account.setTransactions(new ArrayList<>());
            }

            account.getTransactions().add(transaction);
            accountRepository.save(account);
        }
        
        return transaction;
    }
    
    public Account create(Account account) {
        return accountRepository.save(account);
    }
    
    public Account getAccount(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }
}
