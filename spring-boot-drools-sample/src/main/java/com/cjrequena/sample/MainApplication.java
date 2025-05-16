package com.cjrequena.sample;

import com.cjrequena.sample.domain.Account;
import com.cjrequena.sample.domain.Transaction;
import com.cjrequena.sample.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MainApplication {

  public static void main(String[] args) {
    SpringApplication.run(MainApplication.class, args);
  }

  @Bean
  public CommandLineRunner demo(AccountService accountService) {
    return (args) -> {
      // Create accounts
      Account standardAccount = new Account();
      standardAccount.setAccountNumber("123456");
      standardAccount.setCustomerName("John Doe");
      standardAccount.setBalance(1500);
      standardAccount.setAccountType("SAVINGS");
      standardAccount.setPremium(false);
      accountService.create(standardAccount);

      Account premiumAccount = new Account();
      premiumAccount.setAccountNumber("789012");
      premiumAccount.setCustomerName("Jane Smith");
      premiumAccount.setBalance(5000);
      premiumAccount.setAccountType("CHECKING");
      premiumAccount.setPremium(true);
      accountService.create(premiumAccount);

      // Test transactions
      testWithdrawal(accountService, standardAccount, 1200); // Should fail (large withdrawal)
      testWithdrawal(accountService, standardAccount, 500); // Should succeed
      testWithdrawal(accountService, standardAccount, 500); // Should succeed
      testWithdrawal(accountService, standardAccount, 500); // Should succeed
      testWithdrawal(accountService, standardAccount, 500); // Should fail (insufficient funds)
      testDeposit(accountService, standardAccount, 200); // Should succeed
      testWithdrawal(accountService, premiumAccount, 5500); // Should succeed (overdraft allowed)

    };
  }

  private void testWithdrawal(AccountService accountService, Account account, double amount) {
    System.out.println("\nAttempting withdrawal of " + amount + " from account " + account.getAccountNumber());
    Transaction t = new Transaction();
    t.setAccount(account);
    t.setAmount(amount);
    t.setType("WITHDRAWAL");
    t = accountService.process(t);
    System.out.println("Result: " + t.getStatus() +
      (t.getReason() != null ? " (" + t.getReason() + ")" : ""));
    System.out.println("New balance: " + account.getBalance());
  }

  private void testDeposit(AccountService accountService, Account account, double amount) {
    System.out.println("\nAttempting deposit of " + amount + " to account " + account.getAccountNumber());
    Transaction t = new Transaction();
    t.setAccount(account);
    t.setAmount(amount);
    t.setType("DEPOSIT");
    t = accountService.process(t);
    System.out.println("Result: " + t.getStatus());
    System.out.println("New balance: " + account.getBalance());
  }
}
