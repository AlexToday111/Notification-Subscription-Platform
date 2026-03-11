alter table notifications
    alter column status set default 'PENDING';

update notifications
set status = 'PENDING'
where status = 'NEW';

update notifications
set status = 'RETRY_SCHEDULED'
where status = 'RETRYING';

alter table notifications
    add column if not exists next_retry_at timestamptz;

alter table notifications
    add column if not exists correlation_id varchar(120);

create table if not exists delivery_attempts (
    id uuid primary key,
    notification_id uuid not null references notifications(id) on delete cascade,
    attempt_number int not null,
    channel varchar(32) not null,
    status varchar(32) not null,
    started_at timestamptz not null,
    completed_at timestamptz,
    error_code varchar(120),
    error_message varchar(1000),
    response_status int,
    response_diagnostic varchar(1000),
    retryable boolean not null default false,
    correlation_id varchar(120)
);

create index if not exists ix_delivery_attempts_notification_started_at
    on delivery_attempts(notification_id, started_at);
