ALTER TABLE `payment-service`.payment_transactions
    DROP FOREIGN KEY FK_PAYMENTS_ON_DESTBANKACCOUNT;

ALTER TABLE `payment-service`.payment_transactions
    DROP FOREIGN KEY FK_PAYMENTS_ON_SOURCEBANKACCOUNT;

ALTER TABLE `payment-service`.refunds
    DROP FOREIGN KEY FK_REFUNDS_ON_PAYMENT;

CREATE TABLE `payment-service`.currency_accounts
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_at      datetime              NOT NULL,
    updated_at      datetime              NULL,
    version         INT                   NULL,
    balance         DECIMAL               NOT NULL,
    currency        VARCHAR(255)          NOT NULL,
    bank_account_id BIGINT                NOT NULL,
    CONSTRAINT pk_currency_accounts PRIMARY KEY (id)
);

INSERT INTO `payment-service`.currency_accounts (`id`, balance, currency, bank_account_id, created_at)
SELECT bank_accounts.id, bank_accounts.balance, bank_accounts.currency, bank_accounts.id, NOW()
FROM bank_accounts;

ALTER TABLE `payment-service`.payment_transactions
    CHANGE dest_bank_account_id destination_id BIGINT NULL;

ALTER TABLE `payment-service`.payment_transactions
    CHANGE source_bank_account_id source_id BIGINT NULL;

ALTER TABLE `payment-service`.currency_accounts
    ADD CONSTRAINT FK_CURRENCY_ACCOUNTS_ON_BANKACCOUNT FOREIGN KEY (bank_account_id) REFERENCES `payment-service`.bank_accounts (id);

ALTER TABLE `payment-service`.payment_transactions
    ADD CONSTRAINT FK_PAYMENT_TRANSACTIONS_ON_DESTINATION FOREIGN KEY (destination_id) REFERENCES `payment-service`.currency_accounts (id);

ALTER TABLE `payment-service`.payment_transactions
    ADD CONSTRAINT FK_PAYMENT_TRANSACTIONS_ON_SOURCE FOREIGN KEY (source_id) REFERENCES `payment-service`.currency_accounts (id);

ALTER TABLE `payment-service`.refunds
    ADD CONSTRAINT FK_REFUNDS_ON_PAYMENTTRANSACTION FOREIGN KEY (payment_transaction_id) REFERENCES `payment-service`.payment_transactions (id);
