package com.thelearnhub.commercehub.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service registry for the platform. Every business service registers
 * itself here on startup and looks up other services by name instead of
 * hardcoded host:port — this is what makes Feign/Gateway routing work
 * without config changes every time a service moves or scales out.
 */
@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
