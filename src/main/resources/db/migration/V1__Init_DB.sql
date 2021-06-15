create table hibernate_sequence (next_val bigint) engine=InnoDB;
insert into hibernate_sequence values ( 1 );
create table question (
    id integer not null,
    answer varchar(255),
    q_type varchar(255),
    text varchar(255),
    survey_id integer,
    user_id integer,
    primary key (id)
) engine=InnoDB;
create table survey (
    id integer not null,
    description varchar(255),
    end_time datetime(6),
    start_time datetime(6),
    title varchar(255),
    primary key (id)
) engine=InnoDB;
create table usr (
    id integer not null,
    password varchar(255),
    role varchar(255),
    status varchar(255),
    username varchar(255),
    primary key (id)
) engine=InnoDB;
alter table question add constraint FK65ro96jafjvulbqu8ia0pb254 foreign key (survey_id) references survey (id);
alter table question add constraint FK4lejn3ib3xk3bhlmhexvj0pwn foreign key (user_id) references usr (id);