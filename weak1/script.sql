create table courses
(
    id     int auto_increment
        primary key,
    name   varchar(30) not null,
    credit int         not null
);

create table users
(
    id       int auto_increment
        primary key,
    username varchar(20)               not null,
    password varchar(20)               not null,
    role     enum ('student', 'admin') not null,
    constraint username
        unique (username)
);

create table students
(
    id      int auto_increment
        primary key,
    user_id int         not null,
    name    varchar(20) not null,
    phone   varchar(11) null,
    constraint name
        unique (name),
    constraint user_id
        unique (user_id),
    constraint students_ibfk_1
        foreign key (user_id) references users (id)
);

create table student_courses
(
    student_id int not null,
    course_id  int not null,
    primary key (student_id, course_id),
    constraint student_courses_ibfk_1
        foreign key (student_id) references students (id),
    constraint student_courses_ibfk_2
        foreign key (course_id) references courses (id)
);

create index course_id
    on student_courses (course_id);


