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

CREATE TABLE IF NOT EXISTS payment_transactions
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_at     datetime              NOT NULL,
    updated_at     datetime              NULL,
    version        INT                   NULL,
    amount         DECIMAL(38, 2)        NOT NULL,
    status         VARCHAR(255)          NOT NULL,
    source_id      BIGINT                NOT NULL,
    destination_id BIGINT                NULL,
    note           VARCHAR(255)          NULL,
    CONSTRAINT pk_payments PRIMARY KEY (id)
);

create table if not exists payments
(
    id             bigint auto_increment primary key,
    amount         decimal(38, 2)                                                             null,
    order_id       bigint                                                                     null,
    payment_method enum ('BITCOIN', 'CREDIT_CARD', 'MASTERCARD', 'PAYPAL', 'SAMSUNG', 'VISA') null,
    created_at     datetime(6)                                                                not null,
    updated_at     datetime(6)                                                                null,
    version        int                                                                        null
);

CREATE TABLE IF NOT EXISTS refunds
(
    id                     BIGINT AUTO_INCREMENT NOT NULL,
    created_at             datetime              NOT NULL,
    updated_at             datetime              NULL,
    version                INT                   NULL,
    amount                 DECIMAL(38, 2)        NOT NULL,
    status                 VARCHAR(255)          NOT NULL,
    payment_transaction_id BIGINT                NOT NULL,
    reason                 VARCHAR(255)          NULL,
    CONSTRAINT pk_refunds PRIMARY KEY (id)
);
