insert into point values (1,5,null,true,1000000, NOW());
insert into point values (2,6,null,true,1000000, NOW());
insert into point values (3,7,null,true,1000000, NOW());
insert into point values (4,8,null,true,1000000, NOW());
insert into point values (5,9,null,true,1000000, NOW());
insert into point values (6,10,null,true,1000000, NOW());
insert into point values (7,11,null,true,1000000, NOW());
insert into point values (8,12,null,true,1000000, NOW());
insert into point values (9,13,null,true,1000000, NOW());
insert into point values (10,14,null,true,1000000, NOW());
insert into point values (11,15,null,true,1000000, NOW());
insert into point values (12,16,null,true,1000000, NOW());
insert into point values (13,17,null,true,1000000, NOW());
insert into point values (14,18,null,true,1000000, NOW());
insert into point values (15,19,null,true,1000000, NOW());
insert into point values (16,20,null,true,1000000, NOW());

insert into point values (17,5,6,FALSE,10000, NOW());
insert into point values (18,5,7,FALSE,9900, NOW());
insert into point values (19,5,8,FALSE,9800, NOW());
insert into point values (20,5,9,FALSE,9700, NOW());
insert into point values (21,5,10,FALSE,9600, NOW());
insert into point values (22,5,11,FALSE,9500, NOW());
insert into point values (23,5,12,FALSE,9400, NOW());
insert into point values (24,5,13,FALSE,9300, NOW());
insert into point values (25,5,14,FALSE,9200, NOW());
insert into point values (26,5,15,FALSE,9100, NOW());
insert into point values (27,5,16,FALSE,9000, NOW());
insert into point values (28,5,17,FALSE,8900, NOW());
insert into point values (29,5,18,FALSE,8800, NOW());
insert into point values (30,5,19,FALSE,8700, NOW());
insert into point values (31,5,20,FALSE,8600, NOW());
insert into point values (32,5,21,FALSE,8500, NOW());
insert into point values (33,5,22,FALSE,8400, NOW());
insert into point values (34,5,23,FALSE,8300, NOW());

insert into point values (35,6,7,FALSE,30000, NOW());
insert into point values (36,6,8,FALSE,40000, NOW());
insert into point values (37,6,9,FALSE,50000, NOW());


SELECT p.donator_id AS donatorId, SUM(p.point) AS totalPoint FROM point p WHERE p.member_id = :memberId AND p.donator_id is not null GROUP BY p.donator_id ORDER BY SUM(p.point) DESC;
SELECT p.donator_id, SUM(p.point) FROM point p WHERE p.member_id = :memberId AND p.donator_id is not null GROUP BY p.donator_id ORDER BY SUM(p.point) DESC;