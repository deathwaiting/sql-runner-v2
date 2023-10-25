create table car_type(id integer, name varchar);
create table brand(id integer, name varchar);
create table car(id integer, brand_id integer , car_type_id integer, model varchar, purchase_time timestamp);



insert into car_type(id, name)values(1, 'sedan');
insert into car_type(id, name)values(2, 'off road');
insert into car_type(id, name)values(3, 'pickup');

insert into brand(id, name)values(1, 'BMW');
insert into brand(id, name)values(2, 'Land Rover');

insert into car(id, brand_id, car_type_id, model, purchase_time)values(1, 1, 1, 'M3', TIMESTAMP '2020-11-30 20:00:00Z');
insert into car(id, brand_id, car_type_id, model, purchase_time)values(2, 2, 2, 'S4', TIMESTAMP '2020-11-30 20:00:00Z');