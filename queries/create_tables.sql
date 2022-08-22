create table if not exists Users (
    account_id int(30) not null primary key,
    username varchar(20) not null
);

create table if not exists Clients(
    account_id int(30) not null primary key,
    name varchar(40) not null,
    account_expiration date not null,    
    credit_limit int not null,
    debt int not null,
    credit_balance int not null,
    is_company boolean not null
);

create table if not exists Merchants(
    account_id int(30) not null primary key,
    name varchar(40) not null,
    commission int not null,
    customer_profits int not null,
    debt int not null
);

create table if not exists Employees(
     account_id int(30) not null primary key,
     employer_id int not null
);

create table if not exists Transactions(
    id int unsigned auto_increment not null primary key,
    client_id int(30) not null,
    merchant_id int(30) not null,
    transaction_date date not null,
    transaction_amount int not null,
    transaction_type varchar(40) not null,
    as_employee boolean not null
);
