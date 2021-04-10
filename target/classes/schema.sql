CREATE TABLE category
(
    category_number INT AUTO_INCREMENT PRIMARY KEY,
    category_name   varchar(50) not null
);

CREATE TABLE product
(
    id_product      INT AUTO_INCREMENT PRIMARY KEY,
    product_name    varchar(50) not null,
    characteristics varchar(50) not null,
    category_number int         not null
);

alter table if exists product add constraint category_number_fk foreign key (category_number) references category ON DELETE NO ACTION ON UPDATE CASCADE;

CREATE TABLE employee
(
    id_employee     varchar(10) PRIMARY KEY,
    empl_surname    varchar(50)    not null,
    empl_name       varchar(50)    not null,
    empl_patronymic varchar(50)    not null,
    empl_role       varchar(10)    not null,
    salary          decimal(13, 4) not null,
    date_of_birth   date           not null,
    date_of_start   date           not null,
    phone_number    varchar(13)    not null,
    city            varchar(50)    not null,
    street          varchar(50)    not null,
    zip_code        varchar(9)     not null
);

CREATE TABLE customer_card
(
    card_number     INT AUTO_INCREMENT PRIMARY KEY,
    card_surname    varchar(50) not null,
    card_name       varchar(50) not null,
    card_patronymic varchar(50) not null,
    phone_number    varchar(13) not null,
    city            varchar(50) null,
    street          varchar(50) null,
    zip_code        varchar(9) null,
    percent         int         not null
);

CREATE TABLE receipt
(
    check_number INT AUTO_INCREMENT PRIMARY KEY,
    print_date   date           not null,
    sum_total    decimal(13, 4) not null,
    vat          decimal(13, 4) not null,
    id_employee  varchar(10)    not null,
    card_number  int null
);

alter table if exists receipt add constraint id_employee_fk foreign key (id_employee) references employee ON UPDATE CASCADE ON DELETE NO ACTION;
alter table if exists receipt add constraint card_number_fk foreign key (card_number) references customer_card ON UPDATE CASCADE ON DELETE NO ACTION;

CREATE TABLE sale
(
    upc            varchar(12)    not null,
    check_number   int            not null,
    PRIMARY KEY (upc, check_number),
    product_number int            not null,
    selling_price  decimal(13, 4) not null
);

CREATE TABLE store_product
(
    upc                 varchar(12) PRIMARY KEY,
    selling_price       decimal(13, 4) not null,
    products_number     int            not null,
    promotional_product boolean        not null,
    upc_prom            varchar(12) null,
    id_product          int            not null
);

alter table if exists store_product add constraint upc_prom_fk foreign key (upc) references store_product ON UPDATE CASCADE ON DELETE NO ACTION;
alter table if exists store_product add constraint id_product_fk foreign key (id_product) references product ON UPDATE CASCADE ON DELETE NO ACTION;

alter table if exists sale add constraint upc_fk foreign key (upc) references store_product ON UPDATE CASCADE ON DELETE NO ACTION;
alter table if exists sale add constraint check_number_fk foreign key (check_number) references receipt ON UPDATE CASCADE ON DELETE CASCADE;

