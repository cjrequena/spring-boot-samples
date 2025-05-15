package com.cjrequena.sample.service;

import com.cjrequena.sample.domain.Transaction;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RulesService {
    
    @Autowired
    private KieContainer kieContainer;
    
    public Transaction applyRules(Transaction transaction) {
        KieSession kieSession = kieContainer.newKieSession();
        kieSession.insert(transaction);
        kieSession.fireAllRules();
        kieSession.dispose();
        return transaction;
    }
}
