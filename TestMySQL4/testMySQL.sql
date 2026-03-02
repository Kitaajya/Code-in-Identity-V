set @a:='销售部';
set @b:='人力资源部';
set @c:='技术部';
set @d:='主管部';
create database if not exists Company;
use Company;
-- 表中已有数据就删除
truncate table student;
create table if not exists student(
    id int primary key auto_increment,
    name VARCHAR(9),
    department VARCHAR(8),
    phoneNumber LONG
);
alter table student auto_increment=20260001;
insert into student(name,department,phoneNumber)values
('柯南·道尔',@a,15175601727),
('夏洛克·福尔摩斯',@a,15632206335),
('亚当斯·海伦娜',@b,16989856234),
('涅瓦耏·罗斯',@c,15789326562),
('爱丽丝·诺斯顿列',@d,18756523694);
select*from student;
delete from student where id=20260001;
select *from student;
insert into student(name)values ('柯南道尔');
select *from student;
