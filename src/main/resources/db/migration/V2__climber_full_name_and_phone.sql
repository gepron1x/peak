-- Фамилия, отчество и контактный телефон альпиниста

alter table climber rename column name to first_name;

alter table climber
    add column last_name   varchar(100) not null default '',
    add column middle_name varchar(100) not null default '',
    add column phone       varchar(30)  not null default '';

alter table climber
    alter column last_name drop default,
    alter column middle_name drop default,
    alter column phone drop default;
