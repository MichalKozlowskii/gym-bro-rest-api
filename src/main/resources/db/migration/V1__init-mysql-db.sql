drop table if exists exercises;
drop table if exists sets;
drop table if exists users;
drop table if exists workout_plans;
drop table if exists workout_plans_exercises;
drop table if exists workout_plan_sets_reps;
drop table if exists workouts;
create table exercises
(
    creation_date     datetime(6),
    id                bigint       not null auto_increment,
    user_id           bigint       not null,
    demonstration_url varchar(255),
    name              varchar(255) not null,
    primary key (id)
) engine=InnoDB;
create table sets
(
    reps          integer   not null,
    weight        float(53) not null,
    creation_date datetime(6),
    exercise_id   bigint    not null,
    id            bigint    not null auto_increment,
    user_id       bigint    not null,
    workout_id    bigint    not null,
    primary key (id)
) engine=InnoDB;
create table users
(
    enabled  bit          not null,
    id       bigint       not null auto_increment,
    password varchar(255) not null,
    username varchar(255) not null,
    primary key (id)
) engine=InnoDB;
create table workout_plans
(
    creation_date datetime(6),
    id            bigint       not null auto_increment,
    user_id       bigint       not null,
    name          varchar(255) not null,
    primary key (id)
) engine=InnoDB;
create table workout_plans_exercises
(
    exercises_id    bigint not null,
    workout_plan_id bigint not null
) engine=InnoDB;
create table workout_plan_sets_reps
(
    reps            integer,
    sets            integer,
    workout_plan_id bigint not null
) engine=InnoDB;
create table workouts
(
    creation_date   datetime(6),
    id              bigint not null auto_increment,
    user_id         bigint not null,
    workout_plan_id bigint not null,
    primary key (id)
) engine=InnoDB;
alter table users
    add constraint UK_r43af9ap4edm43mmtq01oddj6 unique (username);
alter table exercises
    add constraint FKkiftckymv693t6yxogsb50n4y foreign key (user_id) references users (id);
alter table sets
    add constraint FK4ydywtn7yhc23127mi5382j9b foreign key (exercise_id) references exercises (id);
alter table sets
    add constraint FK7ujbrct6l8j0v3doxdppdnito foreign key (user_id) references users (id);
alter table sets
    add constraint FKqs5vicv32dhh4sym2uow3u0hn foreign key (workout_id) references workouts (id);
alter table workout_plans
    add constraint FK4vr09ve028fenv5fb6e9akqlx foreign key (user_id) references users (id);
alter table workout_plans_exercises
    add constraint FK6xf6q7hn3rg0u7uj8bh14tnl8 foreign key (exercises_id) references exercises (id);
alter table workout_plans_exercises
    add constraint FK16nsdvteg5knxq6bemtqvqets foreign key (workout_plan_id) references workout_plans (id);
alter table workout_plan_sets_reps
    add constraint FK3d6306ef6du43u5766n1orgfp foreign key (workout_plan_id) references workout_plans (id);
alter table workouts
    add constraint FKpf8ql3wbw2drijbk1ugfvki3d foreign key (user_id) references users (id);
alter table workouts
    add constraint FK6wgeggpto1eatcx9ic5l47hq1 foreign key (workout_plan_id) references workout_plans (id);
