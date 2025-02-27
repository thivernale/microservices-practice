CREATE TABLE IF NOT EXISTS order_line_items
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    product_id VARCHAR(255)          NULL,
    sku_code   VARCHAR(255)          NULL,
    price      DECIMAL               NULL,
    quantity   DOUBLE                NOT NULL,
    order_id   BIGINT                NOT NULL,
    CONSTRAINT pk_order_line_items PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS orders
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    `reference`    VARCHAR(255)          NULL,
    total_amount   DECIMAL               NULL,
    payment_method VARCHAR(255)          NULL,
    customer_id    VARCHAR(255)          NULL,
    created_at     datetime              NOT NULL,
    updated_at     datetime              NULL,
    CONSTRAINT pk_orders PRIMARY KEY (id)
);

ALTER TABLE order_line_items
    ADD CONSTRAINT FK_ORDER_LINE_ITEMS_ON_ORDER FOREIGN KEY (order_id) REFERENCES orders (id);
