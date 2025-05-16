package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.Account;
import com.cjrequena.sample.domain.Transaction;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RuleService {

    @Autowired
    private KieContainer kieContainer;

    public Transaction applyRules(Transaction transaction, Account account) {
        KieSession kieSession = kieContainer.newKieSession();
        try {
            kieSession.insert(transaction);
            kieSession.insert(account);
            kieSession.fireAllRules();
        } finally {
            kieSession.dispose();
        }
        return transaction;
    }
}
