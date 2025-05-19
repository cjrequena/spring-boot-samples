package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.Account;
import com.cjrequena.sample.domain.Transaction;
import com.cjrequena.sample.domain.TransactionStatus;
import com.cjrequena.sample.domain.TransactionType;
import com.cjrequena.sample.entity.AccountEntity;
import com.cjrequena.sample.exception.service.AccountNotFoundServiceException;
import com.cjrequena.sample.exception.service.OptimisticConcurrencyServiceException;
import com.cjrequena.sample.exception.service.ServiceException;
import com.cjrequena.sample.mapper.AccountMapper;
import com.cjrequena.sample.repository.AccountRepository;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = ServiceException.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountService {

  private final AccountRepository accountRepository;
  private final RuleService ruleService;
  private final AccountMapper accountMapper;

  public Account create(Account account) {
    AccountEntity entity = accountMapper.toEntity(account);
    AccountEntity savedEntity = accountRepository.save(entity);
    return accountMapper.toDomain(savedEntity);
  }

  public Account retrieveById(Long accountId) throws AccountNotFoundServiceException {
    AccountEntity entity = accountRepository.findById(accountId)
      .orElseThrow(() -> new AccountNotFoundServiceException(
        "The account :: " + accountId + " :: was not found"
      ));
    return accountMapper.toDomain(entity);
  }

  @Transactional(readOnly = true)
  public List<Account> retrieve() {
    return this.accountRepository.findAll().stream()
      .map(this.accountMapper::toDomain)
      .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Account retrieveByAccountNumber(String accountNumber) throws AccountNotFoundServiceException {
    if (accountNumber == null || accountNumber.isBlank()) {
      throw new IllegalArgumentException("Account number must not be null or blank");
    }

    return accountRepository.findByAccountNumber(accountNumber)
      .map(accountMapper::toDomain)
      .orElseThrow(() -> new AccountNotFoundServiceException(
        "Account with number [" + accountNumber + "] not found"
      ));
  }

  @Transactional
  public Account update(Account account) throws AccountNotFoundServiceException, OptimisticConcurrencyServiceException {
      // Retrieve current persisted entity to ensure existence and get managed instance with version
      AccountEntity existingEntity = accountRepository.findById(account.getId())
        .orElseThrow(() -> new AccountNotFoundServiceException("Account not found with ID: " + account.getId()));

      // Update fields from domain to entity without replacing the entity or its version
      accountMapper.updateEntity(account, existingEntity);

      try {
          AccountEntity saved = accountRepository.save(existingEntity);
          return accountMapper.toDomain(saved);
      } catch (OptimisticLockException | ObjectOptimisticLockingFailureException ex) {
          log.trace("Optimistic concurrency control error for account ID {} with expected version {}", account.getId(), existingEntity.getVersion(), ex);
          throw new OptimisticConcurrencyServiceException("Optimistic concurrency control error for account ID " + account.getId() + " with expected version " + existingEntity.getVersion(), ex);
      }
  }


  /**
   * Process a financial transaction, applying business rules and updating account state.
   *
   * @param transaction The transaction to process
   * @return The processed transaction with status and reason (if applicable)
   */
  @Transactional
  public Transaction process(Transaction transaction) throws AccountNotFoundServiceException, OptimisticConcurrencyServiceException {
    log.info("Processing transaction: type={}, amount={}, accountId={}", transaction.getType(), transaction.getAmount(), transaction.getAccountId());

    Account account = retrieveById(transaction.getAccountId());

    // Apply business rules
    transaction = ruleService.applyRules(transaction, account);

    if (TransactionStatus.COMPLETED.getStatus().equalsIgnoreCase(transaction.getStatus())) {
      updateBalance(account, transaction);
      appendTransaction(account, transaction);
      update(account);
      log.info("Transaction completed and account updated. New balance: {}", account.getBalance());
    } else {
      log.warn("Transaction not completed. Status: {}, Reason: {}", transaction.getStatus(), transaction.getReason());
    }

    return transaction;
  }

  // --- Private helpers ---

  private void updateBalance(Account account, Transaction transaction) {
    double balance = account.getBalance();
    if (TransactionType.DEPOSIT.getType().equalsIgnoreCase(transaction.getType())) {
      account.setBalance(balance + transaction.getAmount());
    } else if (TransactionType.WITHDRAWAL.getType().equalsIgnoreCase(transaction.getType())) {
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
