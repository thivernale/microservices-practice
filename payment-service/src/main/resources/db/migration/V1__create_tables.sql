CREATE TABLE IF NOT EXISTS bank_accounts
(
    id          BIGINT AUTO_INCREMENT NOT NULL,
    created_at  datetime              NOT NULL,
    updated_at  datetime              NULL,
    version     INT                   NULL,
    number      VARCHAR(255)          NULL,
    customer_id VARCHAR(255)          NULL,
    balance     DECIMAL               NOT NULL,
    currency    VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_bank_accounts PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS payments
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_at             datetime              NOT NULL,
    updated_at             datetime              NULL,
    version                INT                   NULL,
    amount                 DECIMAL               NOT NULL,
    currency               VARCHAR(255)          NOT NULL,
    payment_method         VARCHAR(255)          NOT NULL,
    order_id               BIGINT                NULL,
    status                 VARCHAR(255)          NOT NULL,
    source_bank_account_id BIGINT                NOT NULL,
    dest_bank_account_id   BIGINT                NULL,
    note                   VARCHAR(255)          NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS refunds
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NULL,
    version    INT                   NULL,
    amount     DECIMAL               NOT NULL,
    currency   VARCHAR(255)          NOT NULL,
    status     VARCHAR(255)          NOT NULL,
    payment_id BIGINT                NOT NULL,
    reason     VARCHAR(255)          NULL,
    CONSTRAINT pk_refunds PRIMARY KEY (id)
);

ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_DESTBANKACCOUNT FOREIGN KEY (dest_bank_account_id) REFERENCES bank_accounts (id);

ALTER TABLE payments
    ADD CONSTRAINT FK_PAYMENTS_ON_SOURCEBANKACCOUNT FOREIGN KEY (source_bank_account_id) REFERENCES bank_accounts (id);

ALTER TABLE refunds
    ADD CONSTRAINT FK_REFUNDS_ON_PAYMENT FOREIGN KEY (payment_id) REFERENCES payments (id);
