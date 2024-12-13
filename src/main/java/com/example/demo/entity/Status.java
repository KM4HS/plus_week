package com.example.demo.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    PENDING,
    APPROVED,
    CANCELED,
    EXPIRED
}
