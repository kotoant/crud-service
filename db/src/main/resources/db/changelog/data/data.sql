--liquibase formatted sql

--changeset Anton.Kotov:data-1 runAlways:true runOnChange:true contextFilter:prod,staging
create temporary table temp_dict_order_status
(
    value int  not null primary key,
    label text not null
);

insert into temp_dict_order_status(value, label)
values (1, 'ACTIVE'),
       (2, 'CLOSED');

insert into dict_order_status (value, label)
select value, label
from temp_dict_order_status
on conflict (value) do update set label = EXCLUDED.label;

delete
from dict_order_status
where value not in (select value from temp_dict_order_status);

drop table temp_dict_order_status;
--rollback select 1;
