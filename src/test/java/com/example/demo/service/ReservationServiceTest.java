package com.example.demo.service;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.RentalLog;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    ReservationRepository reservationRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @Mock
    RentalLogService rentalLogService;

    @Mock
    JPAQueryFactory jpaQueryFactory;

    @Mock
    JPAQuery<Tuple> jpaQuery;

    @InjectMocks
    ReservationService reservationService;

    @Test
    @DisplayName("예약 생성 성공")
    void createReservation_success() {
        // Given
        Long itemId = 1L;
        Long userId = 1L;
        LocalDateTime startAt = LocalDateTime.now();
        LocalDateTime endAt = LocalDateTime.now().plusDays(7L);

        when(reservationRepository.findConflictingReservations(itemId, startAt, endAt)).thenReturn(List.of());
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(new Item()));
        when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
        doNothing().when(rentalLogService).save(any(RentalLog.class));

        // When
        reservationService.createReservation(itemId, userId, startAt, endAt);
    }

    @Test
    @DisplayName("예약 전체 조회")
    void getReservations_success() {
        // Given
        Item mockItem = mock(Item.class);
        User mockUser = mock(User.class);
        Reservation mockReservation = mock(Reservation.class);

        List<Reservation> reservationList = new ArrayList<>();
        reservationList.add(mockReservation);

        when(reservationRepository.findAll()).thenReturn(reservationList);
        when(mockReservation.getUser()).thenReturn(mockUser);
        when(mockReservation.getItem()).thenReturn(mockItem);
        when(mockUser.getNickname()).thenReturn("name");
        when(mockItem.getName()).thenReturn("itemName");

        when(mockReservation.getId()).thenReturn(1L);
        when(mockReservation.getStartAt()).thenReturn(LocalDateTime.now());
        when(mockReservation.getEndAt()).thenReturn(LocalDateTime.now().plusDays(7L));

        // When
        List<ReservationResponseDto> dtos = reservationService.getReservations();

        // Then
        assertThat(dtos).isNotNull();
        assertThat(dtos.get(0).getId()).isEqualTo(1);
        assertThat(dtos.get(0).getNickname()).isEqualTo("name");
    }

    @Test
    @DisplayName("User와 Item으로 예약 목록 검색 성공")
    void searchAndConvertReservations_success() {
        // Given
        List<Reservation> list = new ArrayList<>();
        User mockUser = mock(User.class);
        Item mockItem = mock(Item.class);
        Reservation reservation = new Reservation(mockItem, mockUser, "PENDING", LocalDateTime.now(), LocalDateTime.now().plusDays(7L));
        list.add(reservation);

        when(reservationRepository.searchReservations(1L, 1L)).thenReturn(list);
        when(mockUser.getNickname()).thenReturn("name");
        when(mockItem.getName()).thenReturn("itemName");

        // When
        List<ReservationResponseDto> dtos = reservationService.searchAndConvertReservations(1L, 1L);

        // Then
        assertThat(dtos).isNotNull();
        assertThat(dtos.get(0).getNickname()).isEqualTo("name");
    }
}