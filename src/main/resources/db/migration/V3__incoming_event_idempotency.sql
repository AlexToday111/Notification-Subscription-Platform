create table if not exists incoming_events (
    id uuid primary key,
    external_event_id varchar(160) not null,
    producer varchar(120) not null,
    type varchar(64) not null,
    payload text not null,
    received_at timestamptz not null,
    processed_at timestamptz,
    status varchar(32) not null,
    app_event_id uuid references events(id),
    correlation_id varchar(120),
    constraint uk_incoming_events_producer_external unique (producer, external_event_id)
);

create index if not exists ix_incoming_events_status_received_at
    on incoming_events(status, received_at);
