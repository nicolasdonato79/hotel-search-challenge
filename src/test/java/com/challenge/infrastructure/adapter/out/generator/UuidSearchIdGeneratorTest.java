package com.challenge.infrastructure.adapter.out.generator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UuidSearchIdGeneratorTest {

    private final UuidSearchIdGenerator generator = new UuidSearchIdGenerator();

    @Test
    void shouldGenerateNonNullUuid() {
        String id = generator.generate();

        assertAll(
                () -> assertNotNull(id),
                () -> assertFalse(id.isBlank())
        );
    }

    @Test
    void shouldGenerateDifferentValues() {
        String id1 = generator.generate();
        String id2 = generator.generate();

        assertNotEquals(id1, id2);
    }
}