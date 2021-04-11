package com.lggtt.srb.sms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.lggtt")
public class ServiceSMSApplication {
    public static void main(String[] args) {

        SpringApplication.run(ServiceSMSApplication.class,args);
    }
}
