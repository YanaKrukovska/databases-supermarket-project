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
        'Varbova St. 1', '9536-3869'),
       ('0000000002', 'Poplavskyi', 'Mykhailo', 'Mykhailovych', 'Cashier', 6500, '1949-11-28', '2020-12-01', '+380980623656',
        'Mechyslavka', 'Tiktokova St. 13', '1746-2354'),
        ('0000000003', 'Opanasenko', 'Anastasia', 'Petrivna', 'Cashier', 7000, '1977-12-21', '2017-07-01', '+380684368546',
        'Rakhiv', 'Bandery St. 85', '6647-4325'),
        ('0000000004', 'Dulya', 'Petro', 'Ivanovych', 'Cashier', 6500, '1980-04-13', '2015-04-01', '+380386937596',
        'Drohobych', 'Byzkova St.', '0642-0965'),
        ('0000000005', 'Sternenko', 'Mykhailo', 'Valeriyovych', 'Cashier', 9000, '1949-05-14', '2016-04-01', '+380586937596',
        'Kharkiv', 'Molochna St. 666', '4645-3574'),
        ('0000000006', 'Yurynetz', 'Stepan', 'Ostapovych', 'Cashier', 6500, '2000-06-04', '2020-04-01', '+380675894068',
        'Kyiv', 'Andropova St. 67', '3789-6647'),
        ('0000000007', 'Sobolivna', 'Valentyna', 'Andriivna', 'Cashier', 7500, '1974-09-07', '2019-04-01', '+380867493758',
        'Teteriv', 'Vyshneviy St. 420', '6643-4678'),
        ('0000000008', 'Arkhypenko', 'Valeriy', 'Antonovych', 'Manager', 60000, '1950-11-23', '2013-04-01', '+380685946758',
        'Kyiv', 'Harkivska 168e', '0765-2091'),
        ('0000000009', 'Zibrov', 'Pavlo', 'Mykolayovych', 'Cashier', 6000, '1957-06-22', '2020-12-15', '+380980222626',
        'Kyiv', 'Medova St. 16', '8678-6976');;

INSERT INTO customer_card(card_number, card_surname, card_name, card_patronymic, phone_number, city, street, zip_code,
                          percent)
VALUES (1, 'Bieber', 'Justin', 'Drew', '+380672894758', null, null, null, 5),
       (2, 'West', 'Kanye', 'Omari', '+380963162448', 'Borodyanka', 'Main st. 15', '02896', 6),
       (3, 'West', 'Coast', 'Omarovych', '+380963162448', 'LA', 'Bad st. 14', '01488', 4),
       (4, 'Easter', '_Eggs', 'Happy', '+380968396739', 'Borodyanka', 'Main st. 15', '02280', 3),
       (5, 'Westenko', 'Kostyantyn', 'Omarovych', '+380960548699', 'Kyiv', 'Boolboolyatorna st. 228', '02156', 2),
       (6, 'Yuschenko', 'Voldemar', 'Westenko', '+380912548629', 'Kyiv', 'Boolboolyatorna st. 228', '02156', 1);

INSERT INTO receipt (check_number, print_date, sum_total, vat, id_employee, card_number)
VALUES ('0000000001', '2020-04-01', 37.5, 7.5, '0000000002', 1),
       ('0000000002', '2020-04-01', 228, 45.6, '0000000009', 2),
       ('0000000003', '2020-04-01', 43.5, 8.7, '0000000002', 3),
       ('0000000004', '2020-04-01', 10, 2, '0000000004', 4),
       ('0000000005', '2020-04-01', 312, 62.4, '0000000002', 2),
       ('0000000006', '2020-04-01', 998, 199.6, '0000000005', 5),
       ('0000000007', '2020-04-01', 150.00, 30, '0000000007', 1),
       ('0000000008', '2020-04-02', 2381.99, 476.398, '0000000006', 2),
       ('0000000009', '2020-04-02', 23.50, 4.7, '0000000005', 5),
       ('0000000010', '2020-04-03', 41.23, 8.246, '0000000004', 2),
       ('0000000011', '2020-04-04', 100.01, 20, '0000000002', 4),
       ('0000000012', '2020-04-04', 589.12, 117.824, '0000000003', 4),
       ('0000000013', '2020-05-01', 37.5, 7.5, '0000000003', 5),
       ('0000000014', '2020-05-01', 37.5, 7.5, '0000000009', 6),
       ('0000000015', '2020-04-02', 228, 45.6, '0000000007', 6);

INSERT INTO store_product (upc, selling_price, products_number, promotional_product, upc_prom, id_product)
VALUES ('023100015576', 11.0, 2300, false, null, 1),
       ('761303463011', 7.50, 1200, true, null, 2),
       ('761303463010', 11.50, 1200, false, '761303463011', 3);

INSERT INTO sale (upc, check_number, product_number, selling_price)
VALUES ('761303463011', '0000000001', 2, 7.50),
       ('761303463010', '0000000002', 2, 7.50),
       ('023100015576', '0000000003', 2, 7.50);

