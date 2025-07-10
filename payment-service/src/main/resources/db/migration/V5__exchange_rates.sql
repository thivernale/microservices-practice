CREATE TABLE `payment-service`.exchange_rates
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    created_at datetime              NOT NULL,
    updated_at datetime              NULL,
    version    INT                   NULL,
    rate       DECIMAL(19, 6)        NOT NULL,
    currency   VARCHAR(255)          NOT NULL,
    CONSTRAINT pk_exchange_rates PRIMARY KEY (id)
);

ALTER TABLE `payment-service`.exchange_rates
    ADD CONSTRAINT uk_currency UNIQUE (currency);
