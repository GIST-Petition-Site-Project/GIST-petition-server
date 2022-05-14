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
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.gistpetition.api.petition.domain.QAgreeCount.agreeCount;
import static com.gistpetition.api.petition.domain.QPetition.petition;

@Repository
@RequiredArgsConstructor
public class PetitionQueryDslRepository {
    private final JPQLQueryFactory jpqlQueryFactory;

    public List<PetitionPreviewResponse> findAll(Category category, Predicate predicate) {
        return jpqlQueryFactory.select(buildPetitionPreviewResponse())
                .from(petition)
                .innerJoin(agreeCount)
                .on(petition.id.eq(agreeCount.petitionId))
                .where(categoryEq(category), predicate).fetch();
    }

    public Page<PetitionPreviewResponse> findAll(Category category, Predicate predicate, Pageable pageable) {
        List<PetitionPreviewResponse> results = jpqlQueryFactory.select(buildPetitionPreviewResponse())
                .from(petition)
                .innerJoin(agreeCount)
                .on(petition.id.eq(agreeCount.petitionId))
                .where(categoryEq(category), predicate)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(orderCondition(pageable)).fetch();

        JPQLQuery<Petition> petitionJPQLQuery = jpqlQueryFactory.select(petition)
                .from(petition)
                .innerJoin(agreeCount)
                .on(petition.id.eq(agreeCount.petitionId))
                .where(categoryEq(category), predicate);

        return PageableExecutionUtils.getPage(results, pageable, petitionJPQLQuery::fetchCount);
    }

    public Long count(Category category, Predicate predicate) {
        return jpqlQueryFactory.select(petition)
                .from(petition)
                .innerJoin(agreeCount)
                .on(petition.id.eq(agreeCount.petitionId))
                .where(categoryEq(category), predicate)
                .fetchCount();
    }

    private QPetitionPreviewResponse buildPetitionPreviewResponse() {
        return new QPetitionPreviewResponse(petition.id, petition.title.title, petition.category, petition.status, petition.createdAt, petition.expiredAt, petition.waitingForAnswerAt, agreeCount.count, petition.tempUrl, petition.released, petition.rejection.isNotNull(), petition.answer.isNotNull());
    }

    private BooleanExpression categoryEq(Category category) {
        return category != null ? petition.category.eq(category) : null;
    }

    private OrderSpecifier[] orderCondition(Pageable pageable) {
        PathBuilder<Petition> entityPath = new PathBuilder<>(Petition.class, "petition");
        return pageable.getSort()
                .stream()
                .map(order -> getOrderSpecifier(entityPath, order))
                .toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier getOrderSpecifier(PathBuilder<Petition> entityPath, Sort.Order order) {
        if ("agreeCount".equals(order.getProperty())) {
            return new OrderSpecifier(Order.valueOf(order.getDirection().name()), agreeCount.count);
        }
        return new OrderSpecifier(Order.valueOf(order.getDirection().name()), entityPath.get(order.getProperty()));
    }
}
