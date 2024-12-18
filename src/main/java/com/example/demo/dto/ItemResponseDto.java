package com.example.demo.dto;

import com.example.demo.entity.Item;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemResponseDto {
    private Long id;
    private String name;
    private String description;
    private Long ownerId;
    private Long managerId;

    public ItemResponseDto(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.description = item.getDescription();
        this.ownerId = item.getOwner().getId();
        this.managerId = item.getManager().getId();
    }
}
