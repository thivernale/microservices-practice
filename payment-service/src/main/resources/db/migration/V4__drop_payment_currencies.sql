ALTER TABLE `payment-service`.payment_transactions
    DROP COLUMN currency;

ALTER TABLE `payment-service`.refunds
    DROP COLUMN currency;

ALTER TABLE `payment-service`.payment_transactions
    MODIFY source_id BIGINT NOT NULL;
