package com.darkhub.api.inventory.config;

import com.darkhub.api.inventory.model.Role;
import com.darkhub.api.inventory.model.User;
import com.darkhub.api.inventory.repository.ProductRepository;
import com.darkhub.api.inventory.repository.UserRepository;
import com.darkhub.api.inventory.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("dev")
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               ProductRepository productRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                userRepository.save(User.builder()
                        .username("admin")
                        .email("admin@example.com")
                        .password(passwordEncoder.encode("admin123"))
                        .role(Role.ADMIN)
                        .build());
                userRepository.save(User.builder()
                        .username("demo")
                        .email("demo@example.com")
                        .password(passwordEncoder.encode("demo1234"))
                        .role(Role.USER)
                        .build());
                log.info("Seeded users: admin / admin123 (ADMIN), demo / demo1234 (USER)");
            }

            if (productRepository.count() == 0) {
                productRepository.save(Product.builder().name("Wireless Mouse").description("2.4GHz wireless mouse").price(19.99).build());
                productRepository.save(Product.builder().name("Mechanical Keyboard").description("Hot-swappable RGB keyboard").price(79.90).build());
                productRepository.save(Product.builder().name("USB-C Hub").description("7-in-1 USB-C hub with HDMI").price(35.50).build());
                productRepository.save(Product.builder().name("Webcam 1080p").description("Full HD webcam with microphone").price(49.00).build());
                productRepository.save(Product.builder().name("Laptop Stand").description("Adjustable aluminum laptop stand").price(25.75).build());
                log.info("Seeded 5 sample products");
            }
        };
    }
}