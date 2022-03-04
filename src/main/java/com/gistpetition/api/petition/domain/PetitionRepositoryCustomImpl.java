package com.gistpetition.api.petition.domain;

import com.gistpetition.api.petition.application.PetitionQueryCondition;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import static com.gistpetition.api.petition.domain.QPetition.petition;

@Repository
@RequiredArgsConstructor
public class PetitionRepositoryCustomImpl implements PetitionRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Petition> findAll(PetitionQueryCondition condition, Category category, Instant at, Pageable pageable) {
        QueryResults<Petition> results = jpaQueryFactory.select(petition)
                .from(petition)
                .where(condition.of(category, at))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Petition> petitions = results.getResults();

        return new PageImpl<>(petitions, pageable, results.getTotal());
    }

    @Override
    public Long count(PetitionQueryCondition condition, Category category, Instant at) {
        int count = jpaQueryFactory.select(petition)
                .from(petition)
                .where(condition.of(category, at))
                .fetch().size();
        return (long) count;
    }
}
