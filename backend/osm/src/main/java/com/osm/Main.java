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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Random;
import java.util.UUID;

@SpringBootApplication
public class Main {


    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
    @Bean
    CommandLineRunner runner (
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder
    ){
        return args -> {
            var faker = new Faker();
            Random random = new Random();
            Name name = faker.name();
            String firstName = name.firstName();
            String lastName = name.lastName();
            Gender[] genders = Gender.values();
            Gender gender = genders[random.nextInt(genders.length)];
            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@mail.com";
            Customer customer = new Customer(
                    firstName + " " + lastName,
                    email,
                    passwordEncoder.encode("azerty.123.."),
                    random.nextInt(16,99),
                    gender



            );

            customerRepository.save(customer);
            System.out.println(email);


        };
    }

}

