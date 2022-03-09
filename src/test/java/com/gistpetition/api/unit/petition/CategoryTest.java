package com.gistpetition.api.unit.petition;

import com.gistpetition.api.exception.petition.NoSuchCategoryException;
import com.gistpetition.api.petition.domain.Category;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CategoryTest {
    @Test
    void findCategoryOfExistingId() {
        Category[] values = Category.values();
        for (Category value : values) {
            assertThat(Category.of(value.getId())).isEqualTo(value);
        }
    }

    @Test
    void findCategoryOfNonExistingId() {
        Long NonExistingId = Long.MAX_VALUE;
        assertThatThrownBy(() -> Category.of(NonExistingId)).isInstanceOf(NoSuchCategoryException.class);
    }
}