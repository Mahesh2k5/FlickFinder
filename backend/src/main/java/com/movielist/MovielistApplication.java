package com.movielist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MovielistApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovielistApplication.class, args);
    }
}
