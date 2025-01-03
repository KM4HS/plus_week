package com.example.demo.service;

import com.example.demo.dto.ItemResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
public class ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public ItemService(ItemRepository itemRepository, UserRepository userRepository) {
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ItemResponseDto createItem(String name, String description, Long ownerId, Long managerId) {
        User owner = userRepository.findByIdOrElseThrow(ownerId);
        User manager = userRepository.findByIdOrElseThrow(managerId);

        Item item = new Item(name, description, owner, manager);
        Item saved = itemRepository.save(item);

        return new ItemResponseDto(saved);
    }
}
