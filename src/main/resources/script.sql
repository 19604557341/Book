create table tb_book
(
    book_id     bigint       not null
        primary key,
    book_name   varchar(100) not null,
    category_id bigint       not null,
    price       int          not null,
    inventory   int          not null,
    status      int          not null,
    create_time datetime     null,
    update_time datetime     null,
    create_user bigint       null,
    update_user bigint       null
);

insert into tb_book (book_id, book_name, category_id, price, inventory, status, create_time, update_time, create_user, update_user) values (1, "简爱", 2, 20, 200, 1, "2024-03-29 22:10:20", "2024-03-29 22:10:20",1 , 1);
insert into tb_book (book_id, book_name, category_id, price, inventory, status, create_time, update_time, create_user, update_user) values (2, "追风筝的人", 2, 20, 200, 1, "2024-03-29 22:10:20", "2024-03-29 22:10:20",1 , 1);
insert into tb_book (book_id, book_name, category_id, price, inventory, status, create_time, update_time, create_user, update_user) values (3, "白杨树", 1, 20, 200, 1, "2024-03-29 22:10:20", "2024-03-29 22:10:20",1 , 1);

create table tb_category
(
    category_id   bigint      not null
        primary key,
    category_name varchar(50) not null,
    create_user   bigint      null,
    update_user   bigint      null,
    create_time   datetime    null,
    update_time   datetime    null
);
insert into tb_category (category_id, category_name, create_user, update_user, create_time, update_time) values (1, "言情", 1, 1, "2024-03-29 22:10:20", "2024-03-29 22:10:20");
insert into tb_category (category_id, category_name, create_user, update_user, create_time, update_time) values (2, "文学", 1, 1, "2024-03-29 22:10:20", "2024-03-29 22:10:20");


create table tb_order
(
    order_id     bigint   not null
        primary key,
    create_user  bigint   null,
    update_user  bigint   null,
    create_time  datetime null,
    update_time  datetime null,
    payment_time datetime null,
    status       int      null,
    amount       int      null
);

insert into tb_order (order_id, create_user, update_user, create_time, update_time, payment_time, status, amount) values (1, 1, 1, "2024-03-29 22:10:20", "2024-03-29 22:10:20", "2024-03-29 22:10:20", 1, 40);

create table tb_order_details
(
    order_details_id bigint       not null
        primary key,
    order_id         bigint       not null,
    book_name        varchar(100) not null,
    quantity         int          null,
    price            int          null
);

insert into tb_order_details (order_details_id, order_id, book_name, quantity, price) values (1, 1, "简爱", 1 ,20);
insert into tb_order_details (order_details_id, order_id, book_name, quantity, price) values (2, 1, "追风筝的人", 1 ,20);

create table tb_user
(
    user_id     bigint       not null
        primary key,
    user_name   varchar(50)  not null,
    account     varchar(50)  not null,
    number      bigint       not null,
    sex         int          not null,
    password    varchar(200) not null,
    status      int          not null,
    identity    varchar(10)  null,
    create_time datetime     null,
    update_time datetime     null,
    create_user bigint       null,
    update_user bigint       null
);

insert into tb_user (user_id, user_name, account, number, sex, password, status, identity, create_time, update_time, create_user, update_user) values (1, "管理员", "admin", 19611122233, 1, "e10adc3949ba59abbe56e057f20f883e", 1, "admin", "2024-03-29 22:10:20",  "2024-03-29 22:10:20", 1, 1);

