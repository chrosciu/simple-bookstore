create table authors
(
    id         bigint       not null,
    first_name varchar(255) not null,
    last_name  varchar(255) not null,
    primary key (id),
    unique (first_name, last_name)
);

create table books
(
    id        bigint       not null,
    author_id bigint       not null,
    title     varchar(255) not null,
    pages     int          not null,
    primary key (id),
    unique (author_id, title),
    constraint fk_books_authors
        foreign key (author_id)
            references authors (id)
);

insert into authors(id, first_name, last_name)
values
    (1, 'Adam', 'Mickiewicz'),
    (2, 'Juliusz', 'Słowacki')
;

insert into books(id, author_id, title, pages)
values
    (1, 1, 'Pan Tadeusz', 1000),
    (2, 1, 'Dziady', 200),
    (3, 2, 'Kordian', 100)
;

create sequence authors_seq start with 3;

create sequence books_seq start with 4;