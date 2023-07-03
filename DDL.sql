create table member (
                        member_id bigint auto_increment,
                        created_date timestamp,
                        email varchar(50),
                        gender varchar(10),
                        last_modified_date timestamp,
                        name varchar(30),
                        password varchar(255),
                        phone_number varchar(30),
                        primary key (member_id)
) engine=InnoDB default charset=utf8;

create table member_roles (
                              member_member_id bigint not null,
                              roles varchar(20)
) engine=InnoDB default charset=utf8;

create table reserve (
                         reserve_id bigint auto_increment,
                         created_date timestamp,
                         last_modified_date timestamp,
                         reserve_status varchar(10),
                         reserve_time timestamp,
                         member_id bigint,
                         store_id bigint,
                         primary key (reserve_id)
) engine=InnoDB default charset=utf8;

create table review (
                        review_id bigint auto_increment,
                        rating integer,
                        review_content longtext,
                        member_id bigint,
                        store_id bigint,
                        primary key (review_id)
) engine=InnoDB default charset=utf8;

create table store (
                       store_id bigint auto_increment,
                       city varchar(30),
                       legion varchar(30),
                       street varchar(30),
                       zipcode varchar(30),
                       created_date timestamp,
                       description varchar(255),
                       last_modified_date timestamp,
                       store_name varchar(30),
                       member_id bigint,
                       primary key (store_id)
) engine=InnoDB default charset=utf8;

alter table member_roles
    add constraint memberId
        foreign key (member_member_id)
            references member (member_id);

alter table reserve
    add constraint reserve_foreign_key_memberId
        foreign key (member_id)
            references member (member_id);

alter table reserve
    add constraint reserve_foreign_key_storeId
        foreign key (store_id)
            references store (store_id);

alter table review
    add constraint review_foreign_key_memberId
        foreign key (member_id)
            references member (member_id);

alter table review
    add constraint review_foreign_key_storeId
        foreign key (store_id)
            references store (store_id);

alter table store
    add constraint store_foreign_key_memberId
        foreign key (member_id)
            references member (member_id);
