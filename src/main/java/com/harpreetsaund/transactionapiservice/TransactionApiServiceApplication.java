package com.harpreetsaund.transactionapiservice;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Tag(name = "${project.name}", description = "${project.description}")
public class TransactionApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionApiServiceApplication.class, args);
    }
}
