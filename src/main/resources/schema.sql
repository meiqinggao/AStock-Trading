create table if not exists stock_list (
    id bigint auto_increment,
    code varchar(30),
    stock_name varchar(255),
    recent_zt date,
    recent_touch_zt date,
    market_value decimal(20,5),
    turnover decimal(10,3),
    primary key (id),
    UNIQUE key (code),
    key index_name (stock_name),
    key index_date (recent_zt)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table if not exists stock_concept (
    id bigint auto_increment,
    stock_code varchar(30),
    concept varchar(255),
    concept_type enum('concept', 'first', 'second', 'third'),
    source varchar(255),
    primary key (id),
    foreign key (stock_code) references stock_list (code),
    key index_concept (concept),
    UNIQUE key (stock_code, concept, concept_type)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
