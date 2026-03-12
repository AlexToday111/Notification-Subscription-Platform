create extension if not exists "uuid-ossp";

create table if not exists users (
    id uuid primary key,
    email varchar(320) not null,
    name varchar(120) not null,
    created_at timestamptz not null,
    constraint uk_users_email unique (email)
);

create table if not exists events (
    id uuid primary key,
    type varchar(64) not null,
    payload text not null,
    source varchar(64) not null,
    created_at timestamptz not null
);

create table if not exists subscriptions (
    id uuid primary key,
    user_id uuid not null references users(id),
    channel varchar(32) not null,
    destination varchar(512) not null,
    event_type varchar(64) not null,
    active boolean not null default true,
    created_at timestamptz not null
);

create table if not exists notifications (
    id uuid primary key,
    user_id uuid not null references users(id),
    event_id uuid not null references events(id),
    subscription_id uuid references subscriptions(id),
    channel varchar(32) not null,
    destination varchar(512) not null,
    status varchar(32) not null,
    content varchar(2000) not null,
    error_message varchar(1000),
    retry_count int not null default 0,
    created_at timestamptz not null,
    updated_at timestamptz not null
);

create index if not exists ix_notifications_user_created_at
    on notifications(user_id, created_at);

create index if not exists ix_notifications_status
    on notifications(status);

create index if not exists ix_users_created_at
    on users(created_at);

create index if not exists ix_events_type_created_at
    on events(type, created_at);

create index if not exists ix_subscriptions_user_id
    on subscriptions(user_id);

create index if not exists ix_subscriptions_event_type_active
    on subscriptions(event_type, active);
