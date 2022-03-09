package com.gistpetition.api.petition.domain.repository;

import com.gistpetition.api.petition.domain.Category;
import com.gistpetition.api.petition.domain.Petition;
import com.gistpetition.api.petition.dto.PetitionPreviewResponse;
import com.gistpetition.api.petition.dto.QPetitionPreviewResponse;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.JPQLQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gistpetition.api.petition.domain.QPetition.petition;

@Repository
@RequiredArgsConstructor
public class CustomPetitionRepositoryImpl implements CustomPetitionRepository {
    private final JPQLQueryFactory jpqlQueryFactory;

    @Override
    public Page<PetitionPreviewResponse> findAll(Category category, Predicate predicate, Pageable pageable) {
        List<PetitionPreviewResponse> results = jpqlQueryFactory.select(buildPetitionPreviewResponse())
                .from(petition)
                .where(categoryEq(category), predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderCondition(pageable)).fetch();

        JPQLQuery<Petition> petitionJPQLQuery = jpqlQueryFactory.selectFrom(petition)
                .where(categoryEq(category), predicate);

        return PageableExecutionUtils.getPage(results, pageable, petitionJPQLQuery::fetchCount);
    }

    @Override
    public Long count(Category category, Predicate predicate) {
        return jpqlQueryFactory.selectFrom(petition)
                .where(categoryEq(category), predicate).fetchCount();
    }

    private QPetitionPreviewResponse buildPetitionPreviewResponse() {
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
