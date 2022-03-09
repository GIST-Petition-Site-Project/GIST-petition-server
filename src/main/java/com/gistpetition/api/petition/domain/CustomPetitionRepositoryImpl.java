package com.gistpetition.api.petition.domain;

import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.gistpetition.api.petition.dto.QPetitionPreviewResponse;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

import static com.gistpetition.api.petition.domain.QAnswer.answer;
import static com.gistpetition.api.petition.domain.QPetition.petition;

@RequiredArgsConstructor
@Repository
public class CustomPetitionRepositoryImpl implements CustomPetitionRepository {
    private final JPQLQueryFactory jpqlQueryFactory;

    public Page<PetitionPreviewResponse> findAnsweredPetition(Category category, Pageable pageable) {
        List<PetitionPreviewResponse> results = jpqlQueryFactory.select(petitionPreviewResponse())
                .from(petition)
                .innerJoin(petition.answer, answer)
                .where(categoryEq(category))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderCondition(pageable)).fetch();

        JPQLQuery<Petition> petitionJPQLQuery = jpqlQueryFactory.selectFrom(petition)
                .innerJoin(petition.answer, answer)
                .where()
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderCondition(pageable));
        return PageableExecutionUtils.getPage(results, pageable, () -> petitionJPQLQuery.fetchCount());
    }

    public Page<PetitionPreviewResponse> findOngoingPetition(Instant at, Category category, Pageable pageable) {
        QueryResults<PetitionPreviewResponse> results = jpqlQueryFactory.select(petitionPreviewResponse())
                .from(petition)
                .where(categoryEq(category),
                        petition.released.isTrue(),
                        petition.expiredAt.after(at),
                        petition.id.notIn(JPAExpressions.select(answer.petition.id).from(answer)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderCondition(pageable)).fetchResults();
        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    private QPetitionPreviewResponse petitionPreviewResponse() {
        return new QPetitionPreviewResponse(petition.id, petition.title, petition.category, petition.createdAt, petition.expiredAt, petition.agreeCount, petition.tempUrl, petition.released, petition.answered);
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? petition.category.eq(category) : null;
    }

    private OrderSpecifier[] orderCondition(Pageable pageable) {
        PathBuilder<Petition> entityPath = new PathBuilder<>(Petition.class, "petition");
        return pageable.getSort()
                .stream()
                .map(order -> new OrderSpecifier(Order.valueOf(order.getDirection().name()), entityPath.get(order.getProperty())))
                .toArray(OrderSpecifier[]::new);
    }
}
