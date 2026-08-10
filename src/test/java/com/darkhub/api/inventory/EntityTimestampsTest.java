package com.darkhub.api.inventory;

import com.darkhub.api.inventory.model.Product;
import com.darkhub.api.inventory.model.Role;
import com.darkhub.api.inventory.model.User;
import com.darkhub.api.inventory.repository.ProductRepository;
import com.darkhub.api.inventory.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EntityTimestampsTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void prePersist_doesNotOverwriteExistingCreatedAt() {
        Instant custom = Instant.parse("2026-01-01T00:00:00Z");

        User user = User.builder()
                .username("timed-user")
                .email("timed@example.com")
                .password("whatever")
                .role(Role.USER)
                .createdAt(custom)
                .build();
        User saved = userRepository.save(user);
        assertThat(saved.getCreatedAt()).isEqualTo(custom);

        Product product = Product.builder()
                .name("Timed Product")
                .price(1.0)
                .createdAt(custom)
                .build();
        Product savedProduct = productRepository.save(product);
        assertThat(savedProduct.getCreatedAt()).isEqualTo(custom);
    }
}