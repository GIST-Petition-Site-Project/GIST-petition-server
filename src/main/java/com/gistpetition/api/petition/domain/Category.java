package com.gistpetition.api.petition.domain;

import com.gistpetition.api.exception.petition.NoSuchCategoryException;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Category {
    DORMITORY(1L, "기숙사"),
    FACILITY(2L, "시설운영"),
    CAREER(3L, "진로/취업"),
    ACADEMIC(4L, "학적/교과/장학"),
    STUDENT(5L, "학생지원/행사/동아리"),
    MANAGEMENT(6L, "기획/예산/홍보"),
    COOPERATION(7L, "대외협력"),
    COMMUNICATION(8L, "권익소통"),
    ETC(9L, "기타");

    private final Long id;
    private final String name;

    Category(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public static Category of(Long categoryId) {
        if (!lookup.containsKey(categoryId)) {
            throw new NoSuchCategoryException();
        }
        return lookup.get(categoryId);
    }

    private static final Map<Long, Category> lookup = new HashMap<>();

    static {
        for (Category pc : EnumSet.allOf(Category.class)) {
            lookup.put(pc.getId(), pc);
        }
    }
}
