package br.com.cedran.route;

import com.github.benmanes.caffeine.cache.CaffeineSpec;
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

    @Value("${spring.cache.caffeine.spec:expireAfterAccess=2s}")
    private String caffeineSpec;

    @Bean
    public ExecutorService shortestRouteCalculationExecutorService() {
        return Executors.newFixedThreadPool(executorServiceNoOfThread);
    }

    @Bean
    public CaffeineSpec caffeineSpec() {
        return CaffeineSpec.parse(caffeineSpec);
    }

}
