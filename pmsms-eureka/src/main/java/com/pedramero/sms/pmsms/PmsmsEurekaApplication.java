package com.pedramero.sms.pmsms;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableEurekaServer
public class PmsmsEurekaApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(PmsmsEurekaApplication.class, args);
    }
}
