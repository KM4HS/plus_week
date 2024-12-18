package com.example.demo.controller;

import com.example.demo.config.WebConfig;
import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.service.ReservationService;
import com.example.demo.service.UserService;
import com.example.demo.util.Mapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = ReservationController.class,
        excludeFilters = {@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebConfig.class)}
)
class ReservationControllerTest {

    @MockitoBean
    ReservationService reservationService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("예약 생성 성공")
    void createSuccessTest() throws Exception {
        Mapper<ReservationRequestDto> mapper = new Mapper<>();

        // Given
        Long itemId = 1L;
        Long userId = 1L;
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = LocalDateTime.now().plusDays(7L);

        // When & Then
        mockMvc.perform(post("/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.ToJsonString(new ReservationRequestDto(itemId, userId, startAt, endAt))))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("상태 변경 성공")
    void updateStatusSuccessTest() throws Exception {
        // Given
        String status = "APPROVED";

        // When & Then
        mockMvc.perform(patch("/reservations/{id}/update-status", "1")
                        .content(status))
                .andExpect(status().isOk())
                .andExpect(content().string("상태가 변경되었습니다."));
    }

    @Test
    @DisplayName("상태 변경 실패: 잘못된 status 값")
    void updateStatusFailTest() throws Exception {
        // Given
        String status = "WRONG_STATUS";
        String errMsg = "올바르지 않은 상태: " + status;

        given(reservationService.updateReservationStatus(1L, status)).willThrow(new IllegalArgumentException(errMsg));

        // When & Then
        mockMvc.perform(patch("/reservations/{id}/update-status", "1")
                        .content(status))
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(containsString(errMsg)));
    }

    @Test
    @DisplayName("전체 조회")
    void findAllTest() throws Exception {
        // Given
        List<ReservationResponseDto> list = new ArrayList<>();
        list.add(new ReservationResponseDto(
                1L,
                "name",
                "itemName",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)));

        given(reservationService.getReservations()).willReturn(list);

        // When & Then
        mockMvc.perform(get("/reservations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(equalTo(1)))
                .andExpect(jsonPath("$.[0].nickname").value(equalTo("name")));
    }

    @Test
    @DisplayName("검색 성공")
    void searchTest() throws Exception {
        // Given
        List<ReservationResponseDto> list = new ArrayList<>();
        list.add(new ReservationResponseDto(
                1L,
                "name",
                "itemName",
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(7)));

        given(reservationService.searchAndConvertReservations(1L, 1L)).willReturn(list);

        // When & Then
        mockMvc.perform(get("/reservations/search")
                        .param("userId", "1")
                        .param("itemId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id").value(equalTo(1)))
                .andExpect(jsonPath("$.[0].nickname").value(equalTo("name")));
    }
}