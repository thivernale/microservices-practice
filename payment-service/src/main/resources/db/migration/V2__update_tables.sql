ALTER TABLE payment_transactions
    DROP payment_method,
    DROP order_id;

alter table refunds
    change payment_id payment_transaction_id bigint not null;
