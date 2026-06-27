package com.kolivim.geocompare.repository;

import com.kolivim.geocompare.entity.AddressComparison;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AddressComparisonRepositoryTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("geocompare_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Autowired
    private AddressComparisonRepository repository;

    @Test
    @DisplayName("Успешное сохранение")
    @Transactional
    void shouldSaveAndFindEntity() {
        AddressComparison entity = AddressComparison.builder()
                .address("Москва, Красная площадь, 1")
                .yandexLat(55.7539)
                .yandexLon(37.6208)
                .dadataLat(55.7540)
                .dadataLon(37.6210)
                .distanceMeters(15.0)
                .build();

        AddressComparison saved = repository.save(entity);
        assertThat(saved.getId()).isNotNull();

        AddressComparison found = repository.findById(saved.getId()).orElse(null);
        assertThat(found).isNotNull();
        assertThat(found.getAddress()).isEqualTo(entity.getAddress());
        assertThat(found.getDistanceMeters()).isEqualTo(15.0);
    }

}