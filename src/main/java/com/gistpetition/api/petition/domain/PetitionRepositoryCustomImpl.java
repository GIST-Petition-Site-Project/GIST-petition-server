package com.gistpetition.api.petition.domain;

import com.gistpetition.api.petition.application.PetitionQueryCondition;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
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
    public Page<Petition> findPage(PetitionQueryCondition condition, Instant at, Pageable pageable) {
        QueryResults<Petition> results = jpaQueryFactory.select(petition)
                .from(petition)
                .where(condition.getCondition(at))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<Petition> petitions = results.getResults();

        return new PageImpl<>(petitions, pageable, results.getTotal());
    }

    @Override
    public Page<Petition> findPageByCategory(PetitionQueryCondition condition, Instant at, Pageable pageable, Category category) {
        QueryResults<Petition> results = jpaQueryFactory.select(petition)
                .from(petition)
                .where(condition.getCondition(at), eqCategory(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Petition> petitions = results.getResults();

        return new PageImpl<>(petitions, pageable, results.getTotal());
    }

    @Override
    public Long count(PetitionQueryCondition condition, Instant at) {
        int count = jpaQueryFactory.select(petition)
                .from(petition)
                .where(condition.getCondition(at))
                .fetch().size();
        return (long) count;
    }
//
//    @Override
//    public Page<Petition> findPetitionByAgreeCountIsGreaterThanEqualAndReleasedFalse(int requiredAgreeCount, Pageable pageable) {
//        QueryResults<Petition> results = jpaQueryFactory.select(petition)
//                .from(petition)
//                .where(petition.agreeCount.goe(requiredAgreeCount).and(petition.released.isFalse()))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetchResults();
//
//        List<Petition> petitions = results.getResults();
//
//        return new PageImpl<>(petitions, pageable, results.getTotal());
//    }
//
//    @Override
//    public Page<Petition> findReleasedAndExpiredPetition(Category category, Instant at, Pageable pageable) {
//        QueryResults<Petition> results = jpaQueryFactory.selectFrom(petition)
//                .where(isReleasedPetition(), isExpiredPetition(at), eqCategory(category))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetchResults();
//
//        List<Petition> petitions = results.getResults();
//
//        return new PageImpl<>(petitions, pageable, results.getTotal());
//    }
//
//    @Override
//    public Page<Petition> findReleasedAndUnAnsweredAndUnExpiredPetition(Category category, Instant at, Pageable pageable) {
//        QueryResults<Petition> results = jpaQueryFactory.selectFrom(petition)
//                .where(isReleasedPetition(), isExpiredPetition(at).not(), eqCategory(category))
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetchResults();
//        List<Petition> petitions = results.getResults();
//
//        return new PageImpl<>(petitions, pageable, results.getTotal());
//    }

    private BooleanExpression eqCategory(Category category) {
        return category == null ? null : petition.category.eq(category);
    }
}
