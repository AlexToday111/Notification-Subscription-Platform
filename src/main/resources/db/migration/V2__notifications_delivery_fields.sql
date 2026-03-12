alter table users
    add column if not exists name varchar(120) not null default 'Unknown';

alter table users
    add column if not exists created_at timestamptz not null default now();

alter table events
    add column if not exists source varchar(64) not null default 'api';

alter table subscriptions
    add column if not exists active boolean not null default true;

alter table subscriptions
    add column if not exists created_at timestamptz not null default now();

alter table notifications
    add column if not exists status varchar(32) not null default 'NEW';

alter table notifications
    add column if not exists retry_count integer not null default 0;

alter table notifications
    add column if not exists updated_at timestamptz not null default now();

alter table notifications
    add column if not exists error_message varchar(1000);

do $$
begin
    if exists (
        select 1
        from information_schema.columns
        where table_name = 'notifications'
          and column_name = 'last_error'
    ) then
        execute '
            update notifications
            set error_message = last_error
            where error_message is null
              and last_error is not null
        ';
    end if;
end $$;

create index if not exists ix_notifications_status
    on notifications(status);
