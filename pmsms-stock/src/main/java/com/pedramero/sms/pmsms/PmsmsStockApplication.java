package com.pedramero.sms.pmsms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableDiscoveryClient
public class PmsmsStockApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(PmsmsStockApplication.class, args);
    }
}
