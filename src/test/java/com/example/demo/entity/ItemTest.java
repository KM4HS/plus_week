package com.example.demo.entity;

import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ItemTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    TestEntityManager testEntityManager;

    @Test
    void testStatusNotNull() {

        // Given
        User manager = userRepository.save(new User("admin", "admin@gmail.com", "admin", "Test!1234"));
        User owner = userRepository.save(new User("user", "user@gmail.com", "user", "Test!1234"));

        Item item = new Item("이름", "설명", manager, owner);

        // When
        Item saved = itemRepository.save(item);

        testEntityManager.clear();

        Item findItem = itemRepository.findById(saved.getId()).get();

        // Then
        assertThat(findItem.getStatus()).isNotNull();
        assertThat(findItem.getStatus()).isEqualTo("PENDING");
    }
}