package br.com.cedran.route;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootApplication
@EnableFeignClients
@EnableCaching
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Value("${route-service.executor-service.shortestRouteCalculation.numberOfThreads:30}")
    private Integer executorServiceNoOfThread;

    @Bean
    public ExecutorService shortestRouteCalculationExecutorService() {
        return Executors.newFixedThreadPool(executorServiceNoOfThread);
    }

}
