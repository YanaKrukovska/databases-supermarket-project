INSERT INTO category(category_number, category_name)
VALUES (1, 'Pet Food'),
       (2, 'Fruits'),
       (3, 'Vegetables'),
       (4, 'Milk'),
       (5, 'Crisps');

INSERT INTO product (id_product, product_name, characteristics, category_number)
VALUES (1, 'Whiskas', 'Pink thing', 1),
       (2, 'Felix', 'Whiskas but cheaper', 1),
       (3, 'Banana', 'Yellow, tall and gorgeous', 2),
       (4, 'Smetana', 'As milk but another', 4),
       (5, 'Chips', 'Backed cartopel', 5),
       (6, 'Pivo', 'Mmmm pislya par', 5),
       (7, 'Kabanosy', 'From Dmytruk', 5),
       (8, 'Milk', 'Maybe fresh maybe not', 4);

INSERT INTO role (id_role, name)
VALUES (1, 'ROLE_MANAGER'),
       (2, 'ROLE_CASHIER');

INSERT INTO employee(id_employee, empl_surname, empl_name, empl_patronymic, empl_role, salary, date_of_birth,
                     date_of_start, phone_number, city, street, zip_code, username, password, id_role)
VALUES ('0000000001', 'Vynnyk', 'Oleh', 'Anatoliyovych', 'Manager', 30000, '1973-07-31', '2020-01-01', '+380980123456',
        'Verbibka', 'Varbova St. 1', '9536-3869', 'vynnyk@gmail.com',
        '$2a$10$vx65JDXg5cH.40IwIh01eOmShF6xTuZ8ujE48kzWBdnXaRoccl69m', 1),
       ('0000000002', 'Poplavskyi', 'Mykhailo', 'Mykhailovych', 'Cashier', 6500, '1949-11-28', '2020-12-01',
        '+380980623656',
        'Mechyslavka', 'Tiktokova St. 13', '1746-2354', 'poplavok@gmail.com',
        '$2a$10$vx65JDXg5cH.40IwIh01eOmShF6xTuZ8ujE48kzWBdnXaRoccl69m', 2),
       ('0000000003', 'Opanasenko', 'Anastasia', 'Petrivna', 'Cashier', 7000, '1977-12-21', '2017-07-01',
        '+380684368546',
        'Rakhiv', 'Bandery St. 85', '6647-4325', 'anastasia@gmail.com',
        '$2a$10$vx65JDXg5cH.40IwIh01eOmShF6xTuZ8ujE48kzWBdnXaRoccl69m', 2),
       ('0000000004', 'Dulya', 'Petro', 'Ivanovych', 'Cashier', 6500, '1980-04-13', '2015-04-01', '+380386937596',
        'Drohobych', 'Byzkova St.', '0642-0965', 'dulya@gmail.com',
        '$2a$10$vx65JDXg5cH.40IwIh01eOmShF6xTuZ8ujE48kzWBdnXaRoccl69m', 2),
       ('0000000005', 'Sternenko', 'Mykhailo', 'Valeriyovych', 'Cashier', 9000, '1949-05-14', '2016-04-01',
        '+380586937596',
        'Kharkiv', 'Molochna St. 666', '4645-3574', 'sternenko@gmail.com',
        '$2a$10$vx65JDXg5cH.40IwIh01eOmShF6xTuZ8ujE48kzWBdnXaRoccl69m', 2),
       ('0000000006', 'Yurynetz', 'Stepan', 'Ostapovych', 'Cashier', 6500, '2000-06-04', '2020-04-01', '+380675894068',
        'Kyiv', 'Andropova St. 67', '3789-6647', 'stepan@gmail.com',
        '$2a$10$vx65JDXg5cH.40IwIh01eOmShF6xTuZ8ujE48kzWBdnXaRoccl69m', 2),
       ('0000000007', 'Sobolivna', 'Valentyna', 'Andriivna', 'Cashier', 7500, '1974-09-07', '2019-04-01',
        '+380867493758',
        'Teteriv', 'Vyshneviy St. 420', '6643-4678', 'sobolivna@gmail.com',
        '$2a$10$vx65JDXg5cH.40IwIh01eOmShF6xTuZ8ujE48kzWBdnXaRoccl69m', 2),
       ('0000000008', 'Arkhypenko', 'Valeriy', 'Antonovych', 'Manager', 60000, '1950-11-23', '2013-04-01',
        '+380685946758',
        'Kyiv', 'Harkivska 168e', '0765-2091', 'valera@gmail.com',
        '$2a$10$vx65JDXg5cH.40IwIh01eOmShF6xTuZ8ujE48kzWBdnXaRoccl69m', 1),
       ('0000000009', 'Zibrov', 'Pavlo', 'Mykolayovych', 'Cashier', 6000, '1957-06-22', '2020-12-15', '+380980222626',
        'Kyiv', 'Medova St. 16', '8678-6976', 'mertvibdzholy@gmail.com',
        '$2a$10$vx65JDXg5cH.40IwIh01eOmShF6xTuZ8ujE48kzWBdnXaRoccl69m', 2);

INSERT INTO customer_card(card_number, card_surname, card_name, card_patronymic, phone_number, city, street, zip_code,
                          percent)
VALUES (1, 'Bieber', 'Justin', 'Drew', '+380672894758', null, null, null, 5),
       (2, 'West', 'Kanye', 'Omari', '+380963162448', 'Borodyanka', 'Main st. 15', '02896', 6),
       (3, 'West', 'Coast', 'Omarovych', '+380963162448', 'LA', 'Bad st. 14', '01988', 4),
       (4, 'Easter', 'Eggs', 'Happy', '+380968396739', 'Borodyanka', 'Main st. 15', '02280', 3),
       (5, 'Westenko', 'Kostyantyn', 'Omarovych', '+380960548699', 'Kyiv', 'Boolboolyatorna st. 228', '02156', 2),
       (6, 'Yuschenko', 'Voldemar', 'Westenko', '+380912548629', 'Kyiv', 'Boolboolyatorna st. 228', '02156', 1);

INSERT INTO receipt (check_number, print_date, sum_total, vat, id_employee, card_number)
VALUES (1, '2020-04-01', 14.25, 2.375, '0000000002', 1),
       (2, '2020-04-01', 116.5224, 19.4204, '0000000009', 2),
       (3, '2020-04-01', 772.4352, 128.7392, '0000000002', 3),
       (4, '2020-04-01', 128.2437, 21.374, '0000000004', 4),
       (5, '2020-04-01', 1960.9528, 326.8255, '0000000002', 2),
       (6, '2020-04-01', 3108.266, 518.0443, '0000000005', 5),
       (7, '2020-04-01', 66.462, 11.077, '0000000007', 1),
       (8, '2020-04-02', 121.26	, 20.21, '0000000006', 2),
       (9, '2020-04-02', 6.37, 1.0617, '0000000005', 5),
       (10, '2020-04-03', 117.7256, 19.6209, '0000000004', 2),
       (11, '2020-04-04', 103.8482, 17.308, '0000000002', 4),
       (12, '2020-04-04', 250.3764, 41.7294, '0000000003', 4),
       (13, '2020-05-01', 521.85, 86.975, '0000000003', 5),
       (14, '2020-05-01', 52.6977, 8.783, '0000000009', 6),
       (15, '2020-04-02', 1053.9738, 175.6623, '0000000007', 6),
       (16, '2020-04-02', 165.452, 27.5753, '0000000005', 1),
       (17, '2020-04-03', 233.1012, 38.8502, '0000000004', 2),
       (18, '2020-04-04', 612.6035, 102.1006, '0000000002', 4),
       (19, '2020-04-04', 2164.6617, 360.777, '0000000003', 4),
       (20, '2020-05-01', 1288.21, 214.7017, '0000000003', 5),
       (21, '2020-05-01', 1339.074, 223.179, '0000000009', 6),
       (22, '2020-04-02', 724.68, 120.78, '0000000007', 6);

INSERT INTO store_product (upc, selling_price, products_number, promotional_product, upc_prom, id_product)
VALUES ('023100015576', 11.0, 2300, false, null, 1),
       ('761303463022', 7.50, 300, true, null, 2),
       ('761303463012', 11.20, 1300, false, '761303463022', 2),
       ('761303463013', 33.95, 5000, false, null, 3),
       ('761303463014', 28.65, 1201, false, null, 4),
       ('761303463025', 23.99, 100, true, null, 5),
       ('761303463015', 24.00, 799, false, '761303463025', 5),
       ('761303463016', 18.00, 100000, false, null, 6),
       ('761303463017', 57.80, 234, false, null, 7),
       ('761303463028', 35.67, 5, true, null, 8),
       ('761303463018', 49.90, 1200, false, '761303463028', 8);

INSERT INTO sale (upc, check_number, product_number, selling_price)
VALUES ('761303463022', 1, 2, 7.50),
       ('761303463013', 2, 3, 41.32),
       ('761303463014', 3, 2, 28.65),
       ('761303463015', 4, 1, 52.32),
       ('023100015576', 3, 2, 11.0),
       ('023100015576', 4, 1, 11.0),
       ('761303463017', 5, 4, 57.80),
       ('761303463013', 6, 5, 33.95),
       ('761303463014', 7, 3, 28.65),
       ('761303463013', 8, 2, 64.50),
       ('761303463016', 9, 1, 6.50),
       ('761303463017', 10, 4, 57.80),
       ('761303463018', 11, 2, 49.90),
       ('761303463014', 12, 4, 28.65),
       ('761303463022', 13, 1, 7.50),
       ('761303463018', 14, 1, 49.90),
       ('761303463014', 15, 2, 28.65),
       ('761303463014', 16, 2, 53.53),
       ('761303463016', 17, 100, 18.00),
       ('761303463015', 18, 2, 24.00),
       ('761303463014', 19, 4, 28.65),
       ('761303463017', 20, 3, 57.80),
       ('761303463028', 21, 5, 35.67),
       ('761303463018', 16, 5, 49.90),
       ('761303463022', 17, 1, 3.45),
       ('761303463013', 18, 3, 2.23),
       ('761303463015', 19, 5, 24.00),
       ('761303463013', 20, 13, 21.25),
       ('761303463014', 21, 1, 24.00),
       ('761303463014', 22, 1, 24.01);

