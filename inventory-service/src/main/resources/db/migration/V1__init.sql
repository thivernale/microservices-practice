CREATE TABLE IF NOT EXISTS inventory
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    sku_code VARCHAR(255)          NULL,
    quantity DOUBLE                NOT NULL,
    CONSTRAINT pk_inventory PRIMARY KEY (id)
);
