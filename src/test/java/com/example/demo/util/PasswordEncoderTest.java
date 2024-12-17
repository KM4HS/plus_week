package com.example.demo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordEncoderTest {

    @Test
    @DisplayName("비밀번호 암호화")
    void encode() {

        // Given
        String rawPassword = "Test!1234";

        // When
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        // Then
        assertThat(encodedPassword).isNotNull();
        assertThat(rawPassword).isNotEqualTo(encodedPassword);
    }

    @Test
    @DisplayName("암호화 된 비밀번호와 일치하는지 확인")
    void matches() {

        // Given
        String rawPassword = "Test!1234";
        String encodedPassword = PasswordEncoder.encode(rawPassword);

        // When
        boolean isMatch = PasswordEncoder.matches(rawPassword, encodedPassword);

        // Then
        assertThat(isMatch).isTrue();
    }

    @Test
    void notMatches() {

        // Given
        String rawPassword = "noMatchPassword";
        String encodedPassword = PasswordEncoder.encode("Test!1234");

        // When
        boolean isMatch = PasswordEncoder.matches(rawPassword, encodedPassword);

        // Then
        assertThat(isMatch).isFalse();
    }
}