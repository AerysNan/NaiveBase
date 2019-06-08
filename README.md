# NaiveBase

- [NaiveBase](#naivebase)
  - [Usage](#usage)
    - [Server](#server)
    - [Client](#client)
      - [Console Mode](#console-mode)
      - [GUI Mode](#gui-mode)
  - [Function](#function)
    - [Basic SQL Statement](#basic-sql-statement)
      - [Create Table](#create-table)
      - [Drop Table](#drop-table)
      - [Show Table Schema](#show-table-schema)
      - [Insert](#insert)
      - [Select](#select)
      - [Delete](#delete)
      - [Update](#update)
      - [Subquery](#subquery)
    - [Database Operation](#database-operation)
      - [Create & Drop Database](#create--drop-database)
      - [Switch Database](#switch-database)
      - [Show All Databases](#show-all-databases)
      - [Show All Tables in a database](#show-all-tables-in-a-database)
    - [Advanced SQL Statement](#advanced-sql-statement)
      - [User Action](#user-action)
      - [Authorization Action](#authorization-action)
  - [Furthur Development](#furthur-development)

NaiveBase is a relational database implemented in Java during the course _Database Principles_, 2019 Spring, School of Software, Tsinghua University.

## Usage

- `client.jar` : client running in console mode.
- `clientUI.jar` : client running in GUI mode.
- `server.jar` : database server.

These .jar files can be found in **release**. Both the server and the client depend on `commons-cli-1.4.jar` and `antlr4-runtime-4.7.2.jar`, so make sure they exist under the working directory.

### Server

```
usage: java -jar server.jar
 -h,--help            print help information
 -p,--server <PORT>   server listen port
```

Before starting server, you **must** create a file named `admin.conf` under the working directory, with a single line inside it indicating the administrator's initial password.

### Client

#### Console Mode

```
usage: java -jar client.jar
 -a,--address <IP>       client connect address
 -h,--help               print help information
 -p,--port <PORT>        client read port
 -s,--server <ADDRESS>   server listen address
```

#### GUI Mode

```
usage: java -jar clientUI.jar
 -a,--address <IP>       client connect address
 -h,--help               print help information
 -p,--port <PORT>        client read port
 -s,--server <ADDRESS>   server listen address
```

Once the client starts up, you will be required to input a username and a password. Initially, after the server starts up, there is one database named `admin` and a single user with username `admin` and password given by `admin.conf`. You can only login with the administrator account if you don't create other users.

You can disconnect from server and exit client using `quit` statement.

## Function

NaiveBase's SQL grammar is slightly different from other commonly-used databases. Some examples are listed as follows. To ensure your grammar is correct, please refer to `src/parser/SQL.g4` for further description.

### Basic SQL Statement

#### Create Table

```sql
create table student (
  id int primary key,
  name string ( 10 ),
  age int
);
create table course (
  id int not null,
  name string ( 20 ) not null
);
create table grade (
  student_id int,
  course_id int,
  score double,
  primary key ( student_id, course_id )
);
```

- Supported types : `int, long, float, double, string(x)` where `x` indicates the max length of the string.
- Supported constraints : `primary key, not null` .

#### Drop Table

```sql
drop table if exists student;
drop table student;
```

#### Show Table Schema

```sql
show table student;
```

#### Insert

```sql
insert into student values ( 1, 'A', 21 ), ( 2, 'B', 22 );
insert into student ( id, name ) values ( 3, 'C' );
```

#### Select

```sql
select * from student where id = 1;
select distinct name from course;
select all student.id, course.id from student, course;
select student.name, course.name, score
  from student join course join grade
    on grade.student_id = student.id && grade.course_id = course.id
```

Cartesian product of tables is weakly supported, which means you cannot write a where condition that involves multiple tables. However, as is shown above, on condtion works as an alternative. So we recommand you to write join ... on ... instead of cartesian product ... where ... .

#### Delete

```sql
delete from student where id > 2;
```

#### Update

```sql
update grade set score = score + 20 where id student_id = 2;
```

#### Subquery

We use `view` as an alias of subquery in our database system. Although named as `view`, it is read-only and cannot join wtih other tables.

```sql
create view transcript as
  select student.name, course.name, score
    from student join course join grade
      on grade.student_id = student.id && grade.course_id = course.id
```

### Database Operation

#### Create & Drop Database

```sql
create database school;
drop database if exists school;
drop database school;
```

Only the administrator can perform these actions.

#### Switch Database

```sql
use school;
```

#### Show All Databases

```sql
show databases;
```

#### Show All Tables in a database

```sql
show database school;
```

### Advanced SQL Statement

#### User Action

```sql
create user newuser identified by 'password';
drop user if exists newuser;
drop user newuser;
```

Only the administrator can perform these actions.

#### Authorization Action

```sql
grant select, update on student to newuser;
revoke select, delete on student from newuser;
```

Only the administrator can perform these actions.

## Furthur Development

We intend to use Google's protocol buffer to rewrite the network communication part. However, we failed to install protobuf in Java before due date. We may finish this in future if necessary.
