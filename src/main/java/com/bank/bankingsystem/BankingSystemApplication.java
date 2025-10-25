package com.bank.bankingsystem;

import com.bank.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages={"com.bank"})
@EnableJpaRepositories(basePackages={"com.bank.repository"})
@EntityScan(basePackages={"com.bank.model"})
@EnableConfigurationProperties(value={JwtProperties.class})
@EnableScheduling
public class BankingSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(BankingSystemApplication.class, (String[])args);
        System.out.println("\ud83d\ude80 Banking System Backend is Running...");
    }
}
