select
 c.id as "id",
 typ.name as "car_type",
 brand.name as "brand",
 c.model as "model"
from hr.car c
left join hr.car_type typ on c.car_type_id = typ.id
left join hr.brand on c.brand_id = brand.id