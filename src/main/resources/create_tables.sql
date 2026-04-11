CREATE TABLE hotel_search (
                              id BIGSERIAL PRIMARY KEY,
                              search_id VARCHAR(36) NOT NULL,
                              hotel_id VARCHAR(50) NOT NULL,
                              check_in DATE NOT NULL,
                              check_out DATE NOT NULL,
                              ages_serialized VARCHAR(255) NOT NULL,
                              created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE hotel_search
    ADD CONSTRAINT uk_hotel_search_search_id UNIQUE (search_id);

CREATE INDEX idx_hotel_search_count
    ON hotel_search (hotel_id, check_in, check_out, ages_serialized);