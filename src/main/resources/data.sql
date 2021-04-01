INSERT INTO category(category_number, category_name)
VALUES (1, 'Pet Food'),
       (2, 'Fruits'),
       (3, 'Vegetables');

INSERT INTO product (id_product, product_name, characteristics, category_number)
VALUES (1, 'Whiskas', 'Pink thing', 1),
       (2, 'Felix', 'Whiskas but cheaper', 1),
       (3, 'Bananas', 'Yellow, tall and gorgeous', 2);

INSERT INTO employee(id_employee, empl_surname, empl_name, empl_patronymic, empl_role, salary, date_of_birth,
                     date_of_start, phone_number, city, street, zip_code)
VALUES ('0000000001', 'Vynnyk', 'Oleh', 'Anatoliyovych', 'Manager', 30000, '1973-07-31', '2020-01-01', '+380980123456', 'Verbibka',
        'Varbova St. 1', '1234-3111'),
       ('0000000002', 'Poplavskyi', 'Mykhailo', 'Mykhailovych', 'Cashier', 5000, '1949-11-28', '2021-04-01', '+380980623656',
        'Mechyslavka', 'Tiktokova St. 13', '1234-0189');

INSERT INTO customer_card(card_number, card_surname, card_name, card_patronymic, phone_number, city, street, zip_code,
                          percent)
VALUES (1, 'Bieber', 'Justin', 'Drew', '+380672894758', null, null, null, 5),
       (2, 'West', 'Kanye', 'Omari', '+380963162448', 'Borodyanka', 'Main st. 15', '02896', 50);

INSERT INTO receipt (check_number, print_date, sum_total, vat, id_employee, card_number)
VALUES ('0000000001', '2020-04-01', 37.5, 1, '0000000002', 1);

INSERT INTO store_product (upc, selling_price, products_number, promotional_product, upc_prom, id_product)
VALUES ('023100015576', 11.0, 2300, false, null, 1),
 ('761303463011', 7.50, 1200, true, null, 2),
 ('761303463010', 11.50, 1200, false, '761303463011', 2);

INSERT INTO sale (upc, check_number, product_number, selling_price)
VALUES ('761303463011', '0000000001', 2, 7.50);

