-- Manual DDL without versioning - an intentional legacy problem
-- Executed manually by the DBA in each environment
-- No history, no automated rollback
-- Dev staging, and production environment silently diverge.

CREATE SEQUENCE transaction_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE transactions (
    id              BIGINT PRIMARY KEY DEFAULT nextval('transaction_seq'),
    external_id     VARCHAR(100)    NOT NULL UNIQUE,
    partner_code    VARCHAR(50)     NOT NULL,
    amount          NUMERIC(19, 2)  NOT NULL,
    status          VARCHAR(20)     NOT NULL    -- lose string, without enum constraints
    created_at      TIMESTAMP       NOT NULL,
    updated_at      TIMESTAMP,
    raw_response    TEXT
);

-- Index created without cardinality analysis - intentionally problem.
CREATE INDEX idx_status ON transactions(status);
CREATE INDEX idx_partner_code ON transactions(partner_code);
