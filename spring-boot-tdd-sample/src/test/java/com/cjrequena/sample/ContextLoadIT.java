package com.cjrequena.sample;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ContextLoadIT {

    @Test
    void contextLoad() {
        // Canonical test
        assertTrue(true);
    }
}
