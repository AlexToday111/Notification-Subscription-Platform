create table if not exists outbox_messages (
    id uuid primary key,
    aggregate_type varchar(80) not null,
    aggregate_id varchar(120) not null,
    event_type varchar(80) not null,
    payload text not null,
    status varchar(32) not null,
    created_at timestamptz not null,
    published_at timestamptz,
    retry_count int not null default 0,
    last_error varchar(1000),
    correlation_id varchar(120)
);

create index if not exists ix_outbox_messages_status_created_at
    on outbox_messages(status, created_at);
