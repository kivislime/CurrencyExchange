create sequence exchangerates_id_seq;

alter sequence exchangerates_id_seq owner to myuser;

create table currencies (
                            id        integer generated always as identity primary key,
                            code      varchar(10),
                            full_name varchar(20),
                            sign      varchar(4)
);

create table exchange_rates (
                                id                 integer generated always as identity primary key,
                                base_currency_id   integer references currencies,
                                target_currency_id integer references currencies,
                                rate               numeric(12, 6) check (rate <> 0),
                                unique (base_currency_id, target_currency_id),
                                check (base_currency_id <> target_currency_id)
);

alter sequence exchangerates_id_seq owned by exchange_rates.id;
