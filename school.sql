drop database if exists school;
create database school;
use school;

create table department (
  department_id int,
  department_name string(50),
  primary key (department_id)
);
create table student (
  student_id int,
  department_id int,
  student_name string(10),
  primary key (student_id)
);
create table course (
  course_id int,
  department_id int,
  course_name string(50),
  primary key (course_id)
);
create table grade (
  student_id int,
  course_id int,
  score double,
  primary key (course_id, student_id)
);

insert into department values (01, 'Mathematics'), (02, 'Computer Science'), (03, 'Statistics');

insert into student values (01, 02, 'A');
insert into student values (02, 03, 'B');
insert into student values (03, 02, 'C');
insert into student values (04, 01, 'D');
insert into student values (05, 03, 'E');
insert into student values (06, 01, 'F');
insert into student values (07, 03, 'G');
insert into student values (08, 03, 'H');
insert into student values (09, 02, 'I');
insert into student values (10, 01, 'J');
insert into student values (11, 02, 'K');
insert into student values (12, 01, 'l');

insert into course values (01, 01, 'Linear Algebra');
insert into course values (02, 02, 'Database');
insert into course values (03, 03, 'Financial Econometrics');
insert into course values (04, 01, 'Calculus');
insert into course values (05, 02, 'Assembly');
insert into course values (06, 03, 'Linear Regression');

insert into grade values (01, 01, 84.1), (01, 02, 72.6), (01, 03, 74.4), (01, 04, 84.1), (01, 05, 72.6), (01, 06, 74.4);
insert into grade values (02, 01, 77.9), (02, 02, 69.6), (02, 03, 78.7), (02, 04, 84.1), (02, 05, 72.6), (02, 06, 74.4);
insert into grade values (03, 01, 79.0), (03, 02, 77.9), (03, 03, 81.1), (03, 04, 84.1), (03, 05, 72.6), (03, 06, 74.4);
insert into grade values (04, 01, 67.7), (04, 02, 79.9), (04, 03, 79.0), (04, 04, 84.1), (04, 05, 72.6), (04, 06, 74.4);
insert into grade values (05, 01, 78.8), (05, 02, 81.5), (05, 03, 70.7), (05, 04, 84.1), (05, 05, 72.6), (05, 06, 74.4);
insert into grade values (06, 01, 77.0), (06, 02, 85.2), (06, 03, 76.1), (06, 04, 84.1), (06, 05, 72.6), (06, 06, 74.4);
insert into grade values (07, 01, 79.9), (07, 02, 77.0), (07, 03, 82.9), (07, 04, 84.1), (07, 05, 72.6), (07, 06, 74.4);
insert into grade values (08, 01, 85.0), (08, 02, 82.8), (08, 03, 80.7), (08, 04, 84.1), (08, 05, 72.6), (08, 06, 74.4);
insert into grade values (09, 01, 74.4), (09, 02, 84.6), (09, 03, 89.0), (09, 04, 84.1), (09, 05, 72.6), (09, 06, 74.4);
insert into grade values (10, 01, 79.2), (10, 02, 87.6), (10, 03, 81.9), (10, 04, 84.1), (10, 05, 72.6), (10, 06, 74.4);
insert into grade values (11, 01, 73.1), (11, 02, 75.2), (11, 03, 82.5), (11, 04, 84.1), (11, 05, 72.6), (11, 06, 74.4);
insert into grade values (12, 01, 81.4), (12, 02, 76.9), (12, 03, 78.6), (12, 04, 84.1), (12, 05, 72.6), (12, 06, 74.4);

drop user if exists elder;
create user elder identified by 'naive';
grant select on department to elder;
grant select, update on student to elder;
grant select, delete on grade to elder;
grant select, insert on course to elder;

drop view if exists cs_transcript;
create view cs_transcript as
    select student_name, course_name, score
    from student join grade join course join department
        on student.student_id = grade.student_id && course.course_id = grade.course_id && student.department_id = department.department_id
    where department.department_name = 'Computer Science';