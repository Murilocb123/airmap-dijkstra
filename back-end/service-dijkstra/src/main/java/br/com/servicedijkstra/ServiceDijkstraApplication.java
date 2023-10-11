package br.com.servicedijkstra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

//@EnableSwagger2
@SpringBootApplication
@EnableCaching
@EnableWebMvc
public class ServiceDijkstraApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceDijkstraApplication.class, args);
    }

}
