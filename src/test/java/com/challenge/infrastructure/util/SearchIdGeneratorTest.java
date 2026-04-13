package com.challenge.infrastructure.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SearchIdGeneratorTest {

    private final SearchIdGenerator generator = new SearchIdGenerator();

    @Test
    void shouldGenerateNonNullUuid() {
        String id = generator.generate();

        assertNotNull(id);
        assertFalse(id.isBlank());
    }

    @Test
    void shouldGenerateDifferentValues() {
        String id1 = generator.generate();
        String id2 = generator.generate();

        assertNotEquals(id1, id2);
    }
}