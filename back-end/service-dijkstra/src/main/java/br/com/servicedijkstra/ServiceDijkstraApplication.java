package br.com.servicedijkstra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(value = "br.com.servicedijkstra")
@EnableCaching
public class ServiceDijkstraApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDijkstraApplication.class, args);
    }

}
