CREATE TABLE crew_reviews (
    crew_review_id BIGSERIAL PRIMARY KEY,
    swap_request_id BIGINT NOT NULL UNIQUE REFERENCES swap_requests(swap_request_id) ON DELETE CASCADE,
    crew_id BIGINT NOT NULL,
    rating INTEGER NOT NULL,
    comment VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE INDEX idx_crew_reviews_crew_id ON crew_reviews(crew_id);
