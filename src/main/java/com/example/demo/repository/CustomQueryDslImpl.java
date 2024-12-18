package com.example.demo.repository;


import com.example.demo.entity.Reservation;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

import java.util.List;

import static com.example.demo.entity.QReservation.reservation;

public class CustomQueryDslImpl implements CustomQueryDsl {

    @PersistenceUnit
    EntityManagerFactory emf;

    JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Reservation> searchReservations(Long userId, Long itemId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        JPAQuery<Reservation> query = jpaQueryFactory.select(reservation)
                .from(reservation)
                .leftJoin(reservation.user).fetchJoin()
                .leftJoin(reservation.item).fetchJoin();

        if (userId != null) {
            booleanBuilder.and(reservation.user.id.eq(userId));
        }
        if (itemId != null) {
            booleanBuilder.and(reservation.item.id.eq(itemId));
        }

        return query
                .where(booleanBuilder)
                .fetch();
    }
}
