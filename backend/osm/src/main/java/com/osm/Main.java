package com.osm;


import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.osm.customer.Customer;
import com.osm.customer.CustomerRepository;
import com.osm.customer.Gender;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    @Bean
    CommandLineRunner runner (CustomerRepository customerRepository){
        return args -> {
            var faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            Gender[] genders = Gender.values();
            Gender gender = genders[random.nextInt(genders.length)];
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    firstName.toLowerCase() + "." + lastName.toLowerCase() + "@mail.com",
                    "azerty.123..", random.nextInt(16,99),
                    gender


            );

            customerRepository.save(customer);

        };
    }

}

