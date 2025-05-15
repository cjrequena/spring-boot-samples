package com.cjrequena.sample;

import com.cjrequena.sample.domain.Account;
import com.cjrequena.sample.domain.Transaction;
import com.cjrequena.sample.service.BankingService;
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
  public CommandLineRunner demo(BankingService bankingService) {
    return (args) -> {
      // Create accounts
      Account standardAccount = new Account();
      standardAccount.setAccountNumber("123456");
      standardAccount.setCustomerName("John Doe");
      standardAccount.setBalance(1500);
      standardAccount.setAccountType("SAVINGS");
      standardAccount.setPremium(false);
      bankingService.createAccount(standardAccount);

      Account premiumAccount = new Account();
      premiumAccount.setAccountNumber("789012");
      premiumAccount.setCustomerName("Jane Smith");
      premiumAccount.setBalance(5000);
      premiumAccount.setAccountType("CHECKING");
      premiumAccount.setPremium(true);
      bankingService.createAccount(premiumAccount);

      // Test transactions
      testWithdrawal(bankingService, standardAccount, 1200); // Should fail (large withdrawal)
      testWithdrawal(bankingService, standardAccount, 500); // Should succeed
      testWithdrawal(bankingService, standardAccount, 500); // Should succeed
      testWithdrawal(bankingService, standardAccount, 500); // Should succeed
      testWithdrawal(bankingService, standardAccount, 500); // Should fail (insufficient funds)
      testDeposit(bankingService, standardAccount, 200); // Should succeed
      testWithdrawal(bankingService, premiumAccount, 5500); // Should succeed (overdraft allowed)

    };
  }

  private void testWithdrawal(BankingService bankingService, Account account, double amount) {
    System.out.println("\nAttempting withdrawal of " + amount + " from account " + account.getAccountNumber());
    Transaction t = new Transaction();
    t.setAccount(account);
    t.setAmount(amount);
    t.setType("WITHDRAWAL");
    t = bankingService.processTransaction(t);
    System.out.println("Result: " + t.getStatus() +
      (t.getReason() != null ? " (" + t.getReason() + ")" : ""));
    System.out.println("New balance: " + account.getBalance());
  }

  private void testDeposit(BankingService bankingService, Account account, double amount) {
    System.out.println("\nAttempting deposit of " + amount + " to account " + account.getAccountNumber());
    Transaction t = new Transaction();
    t.setAccount(account);
    t.setAmount(amount);
    t.setType("DEPOSIT");
    t = bankingService.processTransaction(t);
    System.out.println("Result: " + t.getStatus());
    System.out.println("New balance: " + account.getBalance());
  }
}
