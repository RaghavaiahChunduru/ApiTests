SELECT 
    'Rows in sales_fact_1 but missing in sales_fact_2' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_1 s1
LEFT JOIN sales_fact_2 s2
  ON s1.sales_id = s2.sales_id
WHERE s2.sales_id IS NULL

UNION ALL

SELECT 
    'Rows in sales_fact_2 but missing in sales_fact_1' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_2 s2
LEFT JOIN sales_fact_1 s1
  ON s2.sales_id = s1.sales_id
WHERE s1.sales_id IS NULL

UNION ALL

SELECT 
    'product_id mismatch' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_1 s1
JOIN sales_fact_2 s2
  ON s1.sales_id = s2.sales_id
WHERE s1.product_id <> s2.product_id

UNION ALL

SELECT 
    'customer_id mismatch' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_1 s1
JOIN sales_fact_2 s2
  ON s1.sales_id = s2.sales_id
WHERE s1.customer_id <> s2.customer_id

UNION ALL

SELECT 
    'sales_date mismatch' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_1 s1
JOIN sales_fact_2 s2
  ON s1.sales_id = s2.sales_id
WHERE s1.sales_date <> s2.sales_date

UNION ALL

SELECT 
    'quantity mismatch' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_1 s1
JOIN sales_fact_2 s2
  ON s1.sales_id = s2.sales_id
WHERE s1.quantity <> s2.quantity

UNION ALL

SELECT 
    'price_per_unit mismatch' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_1 s1
JOIN sales_fact_2 s2
  ON s1.sales_id = s2.sales_id
WHERE s1.price_per_unit <> s2.price_per_unit

UNION ALL

SELECT 
    'total_amount mismatch' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_1 s1
JOIN sales_fact_2 s2
  ON s1.sales_id = s2.sales_id
WHERE s1.total_amount <> s2.total_amount

UNION ALL

SELECT 
    'created_at mismatch' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_1 s1
JOIN sales_fact_2 s2
  ON s1.sales_id = s2.sales_id
WHERE s1.created_at <> s2.created_at

UNION ALL

SELECT 
    'updated_at mismatch' AS mismatch_type, 
    COUNT(*) AS mismatch_count
FROM sales_fact_1 s1
JOIN sales_fact_2 s2
  ON s1.sales_id = s2.sales_id
WHERE s1.updated_at <> s2.updated_at;
