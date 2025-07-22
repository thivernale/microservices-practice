CREATE TABLE currency_accounts
(
    id              BIGINT AUTO_INCREMENT NOT NULL,
    created_at      datetime              NOT NULL,
    updated_at      datetime              NULL,
    version         INT                   NULL,
    balance         DECIMAL(38, 2)        NOT NULL,
    currency        VARCHAR(255)          NOT NULL,
    bank_account_id BIGINT                NOT NULL,
    CONSTRAINT pk_currency_accounts PRIMARY KEY (id)
);

INSERT INTO currency_accounts (`id`, balance, currency, bank_account_id, created_at)
SELECT bank_accounts.id, bank_accounts.balance, bank_accounts.currency, bank_accounts.id, NOW()
FROM bank_accounts;

ALTER TABLE currency_accounts
    ADD CONSTRAINT FK_CURRENCY_ACCOUNTS_ON_BANKACCOUNT FOREIGN KEY (bank_account_id) REFERENCES bank_accounts (id);

ALTER TABLE payment_transactions
    ADD CONSTRAINT FK_PAYMENT_TRANSACTIONS_ON_DESTINATION FOREIGN KEY (destination_id) REFERENCES currency_accounts (id);

ALTER TABLE payment_transactions
    ADD CONSTRAINT FK_PAYMENT_TRANSACTIONS_ON_SOURCE FOREIGN KEY (source_id) REFERENCES currency_accounts (id);

ALTER TABLE refunds
    ADD CONSTRAINT FK_REFUNDS_ON_PAYMENTTRANSACTION FOREIGN KEY (payment_transaction_id) REFERENCES payment_transactions (id);
