package com.alextim;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.management.ManagementFactory;

@SpringBootApplication()
public class Main {

    public static void main(String[] args) {
        System.out.println("pid: " + ManagementFactory.getRuntimeMXBean().getName());
        ConfigurableApplicationContext applicationContext = SpringApplication.run(Main.class);
    }


}
