package com.example.demo.service;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.*;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

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

    @InjectMocks
    ReservationService reservationService;

    LocalDateTime startAt = LocalDateTime.now();
    LocalDateTime endAt = LocalDateTime.now().plusDays(7L);

    @Test
    @DisplayName("예약 생성 성공")
    void create_success() {
        // Given
        Long itemId = 1L;
        Long userId = 1L;

        when(reservationRepository.findConflictingReservations(itemId, startAt, endAt)).thenReturn(List.of());
        when(itemRepository.findByIdOrElseThrow(itemId)).thenReturn(new Item());
        when(userRepository.findByIdOrElseThrow(userId)).thenReturn(new User());
        doNothing().when(rentalLogService).save(any(RentalLog.class));

        // When
        reservationService.createReservation(itemId, userId, startAt, endAt);
    }

    @Test
    @DisplayName("예약 생성 실패: 이미 예약된 물건")
    void create_alreadyReserved() {
        Reservation mockReservation = mock(Reservation.class);
        given(reservationRepository.findConflictingReservations(1L, startAt, endAt))
                .willReturn(List.of(mockReservation));

        ReservationConflictException err = assertThrows(ReservationConflictException.class,
                () -> reservationService.createReservation(1L, 1L, startAt, endAt));

        assertThat(err.getMessage()).isEqualTo("해당 물건은 이미 그 시간에 예약이 있습니다.");
    }

    @Test
    @DisplayName("예약 생성 실패: 아이템을 찾을 수 없음")
    void create_noItem() {
        willThrow(new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다.")).given(itemRepository).findByIdOrElseThrow(1L);

        IllegalArgumentException err = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(1L, 1L, startAt, endAt));

        assertThat(err.getMessage()).isEqualTo("해당 ID에 맞는 값이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("예약 생성 실패: 유저를 찾을 수 없음")
    void create_noUser() {
        willThrow(new IllegalArgumentException("해당 ID에 맞는 값이 존재하지 않습니다.")).given(userRepository).findByIdOrElseThrow(1L);

        IllegalArgumentException err = assertThrows(IllegalArgumentException.class,
                () -> reservationService.createReservation(1L, 1L, startAt, endAt));

        assertThat(err.getMessage()).isEqualTo("해당 ID에 맞는 값이 존재하지 않습니다.");
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

    @Test
    @DisplayName("예약 상태 업데이트: 성공")
    void update_success() throws NoSuchFieldException, IllegalAccessException {
        User mockUser = mock(User.class);
        Item mockItem = mock(Item.class);
        Reservation reservation = new Reservation(mockItem, mockUser, Status.PENDING.name(), startAt, endAt);
        String updateStatus = Status.APPROVED.name();

        given(mockUser.getNickname()).willReturn("name");
        given(mockItem.getName()).willReturn("itemName");

        // TODO: 리플렉션 사용이 좋을지, Reservation에 생성자를 추가하는 게 좋을지 고민해볼 것...
        // 리플렉션으로 id 값 설정
        Field idField = Reservation.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(reservation, 1L);

        given(reservationRepository.findByIdOrElseThrow(1L)).willReturn(reservation);

        ReservationResponseDto dto = reservationService.updateReservationStatus(1L, updateStatus);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("예약 상태 업데이트: 올바른 상태 전달")
    void update_availableStatus() {
        // Given
        String validStatus = "APPROVED";
        Status expectedStatus = Status.APPROVED;

        // When
        Status statusToChange = Arrays.stream(Status.values())
                .filter(s -> s.name().equals(validStatus))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 상태: " + validStatus));

        // Then
        assertThat(statusToChange).isEqualTo(expectedStatus);
    }

    @Test
    @DisplayName("예약 상태 업데이트: 잘못된 상태값 전달")
    void update_invalidStatus() {
        // Given
        String validStatus = "WRONG_STATUS";

        // When
        IllegalArgumentException err = assertThrows(IllegalArgumentException.class,
                () -> {
                    Arrays.stream(Status.values())
                            .filter(s -> s.name().equals(validStatus))
                            .findAny()
                            .orElseThrow(() -> new IllegalArgumentException("올바르지 않은 상태: " + validStatus));
                });

        // Then
        assertThat(err.getMessage()).isEqualTo("올바르지 않은 상태: " + validStatus);
    }

    @Test
    @DisplayName("예약 상태 업데이트: PENDING 외의 상태가 Expired 또는 APPROVED 로 변경 시도")
    void update_toExpiredFail() {
        // Given
        Reservation mockReservation = mock(Reservation.class);
        given(mockReservation.getStatus()).willReturn(Status.CANCELED.name());
        given(reservationRepository.findByIdOrElseThrow(1L)).willReturn(mockReservation);
        String status = "APPROVED";

        // When & Then
        IllegalArgumentException err = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(1L, status);
        });

        // 예외 메시지 확인
        assertThat(err.getMessage()).isEqualTo("PENDING상태만 APPROVED로 변경 가능합니다.");
    }

    @Test
    @DisplayName("예약 상태 업데이트: EXPIRED 취소 시도")
    void update_toCancelFail() {
        // Given
        Reservation mockReservation = mock(Reservation.class);
        given(mockReservation.getStatus()).willReturn(Status.EXPIRED.name());
        given(reservationRepository.findByIdOrElseThrow(1L)).willReturn(mockReservation);
        String status = "CANCELED";

        // When & Then
        IllegalArgumentException err = assertThrows(IllegalArgumentException.class, () -> {
            reservationService.updateReservationStatus(1L, status);
        });

        assertThat(err.getMessage()).isEqualTo(Status.EXPIRED.name() + "상태인 예약은 취소할 수 없습니다.");
    }
}