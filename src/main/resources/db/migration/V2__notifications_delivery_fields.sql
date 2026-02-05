alter table notifications
    add column if not exists status varchar(32) not null default 'NEW';

alter table notifications
    add column if not exists retry_count integer not null default 0;

alter table notifications
    add column if not exists updated_at timestamptz not null default now();

create index if not exists ix_notifications_status
    on notifications(status);
