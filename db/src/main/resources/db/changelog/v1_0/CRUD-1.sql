--liquibase formatted sql

--changeset Anton.Kotov:CRUD-1-0
create table if not exists dict_order_status
(
    value int  not null primary key,
    label text not null
);
--rollback drop table if exists dict_order_status;

--changeset Anton.Kotov:CRUD-1-1
create table if not exists "order"
(
    id             bigserial                not null primary key,
    public_id      uuid                     not null,
    version        int                      not null check ( version > 0 ),
    latest_version boolean                  not null,
    created_at     timestamp with time zone not null, -- audit
    updated_at     timestamp with time zone not null, -- audit
    created        timestamp with time zone not null, -- business date for search
    status         int references dict_order_status (value)
);
--rollback drop table if exists "order";

--changeset Anton.Kotov:CRUD-1-2
create unique index order_public_id_version_key on "order" (public_id, version);
--rollback drop index order_public_id_version_key;

--changeset Anton.Kotov:CRUD-1-3
create unique index order_public_id_latest_version_true_key on "order" (public_id)
    where latest_version = true;
--rollback drop index order_public_id_latest_version_true_key;

--changeset Anton.Kotov:CRUD-1-4
create index order_created_public_id_idx on "order" (created, public_id)
    where latest_version = true;
--rollback drop index order_created_public_id_idx;

--changeset Anton.Kotov:CRUD-1-5
create index order_created_public_id_status_1_idx on "order" (created, public_id)
    where latest_version = true and status = 1;
--rollback drop index order_created_public_id_status_1_idx;
