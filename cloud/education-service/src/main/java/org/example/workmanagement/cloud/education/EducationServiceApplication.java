package org.example.workmanagement.cloud.education;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cloud.openfeign.EnableFeignClients;

// Keep the skeleton bootable before a real MySQL instance and JPA model are wired in.
@EnableFeignClients
@SpringBootApplication
public class EducationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(EducationServiceApplication.class, args);
    }
}
