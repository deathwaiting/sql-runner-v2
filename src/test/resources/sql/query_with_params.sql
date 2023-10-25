select
 c.id as "id",
 typ.name as "car_type",
 br.name as "brand",
 c.model as "model"
from car c
left join car_type typ on c.car_type_id = typ.id
left join brand br on c.brand_id = br.id
where br.name = :brand_name