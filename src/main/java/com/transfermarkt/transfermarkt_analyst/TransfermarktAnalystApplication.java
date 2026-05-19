package com.transfermarkt.transfermarkt_analyst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class TransfermarktAnalystApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransfermarktAnalystApplication.class, args);
    }


}