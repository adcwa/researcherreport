alter table t1 add (flag char(1));
alter table t1 add (year varchar2(100));
alter table t2 add (flag2 char(1));
alter table t2 add (year2 varchar2(100));
-- alter table t1 add (id number(10));
-- alter table t2 add (id number(10));
alter table t1 add(mc number(4));
select to_char(tt1."accper",'YYYY-MM-dd'),tt1."accper", substr(to_char(tt1."accper",'YYYY-MM-dd'),0,4) from t1 tt1 ;
-- select  substr(tt2."Rptdt",0,4) ,tt2."Brokern" from t2 tt2 ;

update t1 set year = substr(to_char("accper",'YYYY-MM-dd'),0,4);
update t2 set year2 = substr("Rptdt",0,4);

-- t1中分析人没有逗号分隔
-- select * from t1 where t1."institutionid" like '%,%';

update   t1  tt1 set tt1.id = rownum ;
update   t2  tt2 set tt2.id = rownum ;

select * from t1 where flag = 1;
select * from t2 where flag2 = 1;
--有7455 条数据没有分析师
select * from t2 where "AnanmID" is null;
--387个InstitutionID 为空
select * from t2 where "InstitutionID" is null;

select * from t1 where "institutionid" is null;
--有4700多数据没有分析师
-- select count(1)   from t1 where "ananm" is not  null;

select count(1) from t2 ;
select count(1) from t1 ;
--从分析师表中获取证券公司的编码
select * from t1 where "brokern"  ='湘财证券有限责任公司'; --104098
select * from t1 where "brokern"  ='国泰君安证券股份有限公司'; --106064
select * from t1 where "brokern"  ='光大证券股份有限公司'; --104059
select * from t1 where "brokern"  ='平安证券有限责任公司'; --104149
select * from t1 where "brokern"  ='平安证券股份有限公司'; --104149
--统一平安的名称
update t2 set "Brokern"  ='平安证券股份有限公司' where "Brokern" ='平安证券有限责任公司';
update t1 set "brokern"  ='平安证券股份有限公司' where "brokern" ='平安证券有限责任公司';
--更新预测表中缺失的结构编码
update t2 set "InstitutionID" = '104098' where "Brokern" ='湘财证券有限责任公司';
update t2 set "InstitutionID" = '106064' where "Brokern" ='国泰君安证券股份有限公司';
update t2 set "InstitutionID" = '104059' where "Brokern" ='光大证券股份有限公司';
update t2 set "InstitutionID" = '104149' where "Brokern" ='平安证券股份有限公司';

select DISTINCT "brokern","institutionid" from t1 ;

create table userinfo as select DISTINCT "ananm" ,"ananmid" from t1;
select * from userinfo;--6733个用户
select DISTINCT "ananm" from userinfo;--没有重复名字 5392个不重复的名字， 所有有6733 -5392 = 2341个用户重名
update t2 set "AnanmID" = (select "ananmid" from userinfo t1 where t1."ananm"  ="Ananm");

drop table userinfo;
create table userinfo as select DISTINCT "ananm" ,"ananmid" ,"brokern" from  t1;
select * from userinfo;--8282个用户
select DISTINCT "ananm" ,"brokern" from userinfo;--7985 个区分机构后唯一的用户名， 所以还是有分析师在一个机构下重名 ， 数量为8282 - 7985  = 297 个
--方案，将没有ID 的分析师忽略掉 7455 条记录
select * from t2 where "AnanmID" is null;
delete from t2 where "AnanmID" is null;


select * from t1 where flag =1 ; --18947 个分析师符合 ，总共19369 个分析师数据
select * from t2 where flag2 =1; --970363 个预测报告符合 ， 总共1024915个预测报告





create table T3 as select * from t1 tt1 full outer join t2 tt2 on tt1.year = tt2.year2 and （tt1."ananm" = tt2."Ananm1" or tt1."ananm" = tt2."Ananm2" or tt1."ananm" = tt2."Ananm3" or tt1."ananm" = tt2."Ananm4" or tt1."ananm" = tt2."Ananm5" or tt1."ananm" = tt2."Ananm6" or tt1."ananm" = tt2."Ananm7" or tt1."ananm" = tt2."Ananm8"）  and tt1."brokern" = tt2."Brokern";

create table T5 as select * from t1 tt1 left  join t2 tt2 on tt1.year = tt2.year2 and tt1."ananm" is not null and （tt1."ananm" = tt2."Ananm1" or tt1."ananm" = tt2."Ananm2" or tt1."ananm" = tt2."Ananm3" or tt1."ananm" = tt2."Ananm4" or tt1."ananm" = tt2."Ananm5" or tt1."ananm" = tt2."Ananm6" or tt1."ananm" = tt2."Ananm7" or tt1."ananm" = tt2."Ananm8"）  and tt1."brokern" = tt2."Brokern";

select * from T5;

create table T4 as select * from T3 where "ananm" is not null;



create table T6 as select * from t2 tt2 left  join t1 tt1 on tt1.year = tt2.year2 and tt1."ananm" is not null and （tt1."ananm" = tt2."Ananm1" or tt1."ananm" = tt2."Ananm2" or tt1."ananm" = tt2."Ananm3" or tt1."ananm" = tt2."Ananm4" or tt1."ananm" = tt2."Ananm5" or tt1."ananm" = tt2."Ananm6" or tt1."ananm" = tt2."Ananm7" or tt1."ananm" = tt2."Ananm8"）  and tt1."institutionid" = tt2."InstitutionID";

select count(1) from t1;

update t1 tt1 set tt1.mc =(select count(1) from t2 tt2 where tt1.year = tt2.year2 and tt1."ananm" is not null and （tt1."ananm" = tt2."Ananm1" or tt1."ananm" = tt2."Ananm2" or tt1."ananm" = tt2."Ananm3" or tt1."ananm" = tt2."Ananm4" or tt1."ananm" = tt2."Ananm5" or tt1."ananm" = tt2."Ananm6" or tt1."ananm" = tt2."Ananm7" or tt1."ananm" = tt2."Ananm8"）  and tt1."brokern" = tt2."Brokern")
where exists(select count(1) from t2 tt2 where tt1.year = tt2.year2 and tt1."ananm" is not null and （tt1."ananm" = tt2."Ananm1" or tt1."ananm" = tt2."Ananm2" or tt1."ananm" = tt2."Ananm3" or tt1."ananm" = tt2."Ananm4" or tt1."ananm" = tt2."Ananm5" or tt1."ananm" = tt2."Ananm6" or tt1."ananm" = tt2."Ananm7" or tt1."ananm" = tt2."Ananm8"）  and tt1."brokern" = tt2."Brokern");


