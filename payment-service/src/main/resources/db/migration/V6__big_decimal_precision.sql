ALTER TABLE `payment-service`.payment_transactions
    MODIFY amount DECIMAL(38, 2);

ALTER TABLE `payment-service`.payments
    MODIFY amount DECIMAL(38, 2);

ALTER TABLE `payment-service`.refunds
    MODIFY amount DECIMAL(38, 2);

ALTER TABLE `payment-service`.currency_accounts
    MODIFY balance DECIMAL(38, 2);
