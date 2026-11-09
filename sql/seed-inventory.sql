-- Local demo data only. Store codes, SKUs and locations are fictional.
-- Re-running preserves existing inventory quantities.
USE inventory;

CREATE TABLE IF NOT EXISTS stores (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state CHAR(2) NOT NULL
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    sku VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(100) NOT NULL,
    unit VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS inventory (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    store_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity_on_hand INT NOT NULL DEFAULT 0,
    reorder_point INT NOT NULL DEFAULT 5,
    version BIGINT NOT NULL DEFAULT 0,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT uq_inventory_store_product UNIQUE (store_id, product_id),
    CONSTRAINT fk_inventory_store FOREIGN KEY (store_id) REFERENCES stores(id),
    CONSTRAINT fk_inventory_product FOREIGN KEY (product_id) REFERENCES products(id),
    CONSTRAINT chk_inventory_quantity CHECK (quantity_on_hand >= 0),
    CONSTRAINT chk_inventory_reorder CHECK (reorder_point >= 0)
);

START TRANSACTION;

INSERT INTO stores (store_code, name, city, state)
SELECT seed.store_code, seed.name, seed.city, 'IL'
FROM (
    SELECT 'DEMO-CHI-01' AS store_code, 'Demo Chicago Downtown' AS name, 'Chicago' AS city
    UNION ALL SELECT 'DEMO-CHI-02', 'Demo Chicago North', 'Chicago'
    UNION ALL SELECT 'DEMO-EVN-01', 'Demo Evanston', 'Evanston'
) seed
WHERE NOT EXISTS (SELECT 1 FROM stores s WHERE s.store_code = seed.store_code);

INSERT INTO products (sku, name, category, unit)
SELECT seed.sku, seed.name, seed.category, seed.unit
FROM (
    SELECT 'DEMO-P001' AS sku, 'Whole Milk 1 Gallon' AS name, 'Grocery' AS category, 'bottle' AS unit
    UNION ALL SELECT 'DEMO-P002', 'Breakfast Cereal 18 oz', 'Grocery', 'box'
    UNION ALL SELECT 'DEMO-P003', 'Ground Coffee 12 oz', 'Grocery', 'bag'
    UNION ALL SELECT 'DEMO-P004', 'Laundry Detergent 92 fl oz', 'Household', 'bottle'
    UNION ALL SELECT 'DEMO-P005', 'Paper Towels 6 Rolls', 'Household', 'pack'
    UNION ALL SELECT 'DEMO-P006', 'Toothpaste 6 oz', 'Personal Care', 'tube'
    UNION ALL SELECT 'DEMO-P007', 'Cotton T-Shirt Medium', 'Clothing', 'piece'
    UNION ALL SELECT 'DEMO-P008', 'Building Blocks 100 Pieces', 'Toys', 'box'
    UNION ALL SELECT 'DEMO-P009', 'Wireless Headphones', 'Electronics', 'piece'
    UNION ALL SELECT 'DEMO-P010', 'Spiral Notebook 100 Sheets', 'Stationery', 'piece'
) seed
WHERE NOT EXISTS (SELECT 1 FROM products p WHERE p.sku = seed.sku);

INSERT INTO inventory (store_id, product_id, quantity_on_hand, reorder_point)
SELECT s.id, p.id,
    CASE
        WHEN s.store_code = 'DEMO-CHI-01' AND p.sku = 'DEMO-P001' THEN 0
        WHEN s.store_code = 'DEMO-CHI-02' AND p.sku = 'DEMO-P009' THEN 2
        WHEN s.store_code = 'DEMO-EVN-01' AND p.sku = 'DEMO-P005' THEN 4
        ELSE 15 + MOD(CAST(RIGHT(p.sku, 3) AS UNSIGNED) * 7
            + CASE s.store_code WHEN 'DEMO-CHI-01' THEN 0 WHEN 'DEMO-CHI-02' THEN 11 ELSE 23 END, 60)
    END,
    CASE WHEN p.category = 'Electronics' THEN 5 ELSE 10 END
FROM stores s CROSS JOIN products p
WHERE s.store_code IN ('DEMO-CHI-01', 'DEMO-CHI-02', 'DEMO-EVN-01')
  AND p.sku IN ('DEMO-P001', 'DEMO-P002', 'DEMO-P003', 'DEMO-P004', 'DEMO-P005',
                'DEMO-P006', 'DEMO-P007', 'DEMO-P008', 'DEMO-P009', 'DEMO-P010')
  AND NOT EXISTS (SELECT 1 FROM inventory i WHERE i.store_id = s.id AND i.product_id = p.id);

COMMIT;

SELECT s.store_code, p.sku, p.name, i.quantity_on_hand, i.reorder_point
FROM inventory i
JOIN stores s ON s.id = i.store_id
JOIN products p ON p.id = i.product_id
ORDER BY s.store_code, p.sku;
