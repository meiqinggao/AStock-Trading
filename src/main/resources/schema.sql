create table if not exists stock_list (
    id varchar(30),
    stock_name varchar(255),
    recent_zt date,
    primary key (id),
    key index_name (stock_name),
    key index_date (recent_zt)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists stock_concept (
    id bigint auto_increment,
    stock_code varchar(30),
    concept varchar(255),
    primary key (id),
    foreign key (stock_code) references stock_list (id),
    key index_concept (concept)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
