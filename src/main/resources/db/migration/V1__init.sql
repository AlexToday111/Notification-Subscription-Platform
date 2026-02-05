create extension if not exists "uuid-ossp";

create table if not exists users (
                                     id uuid primary key,
                                     email varchar(255) not null
    );

create table if not exists events (
                                      id uuid primary key,
                                      type varchar(128) not null,
    payload jsonb,
    created_at timestamptz not null
    );

create table if not exists subscriptions (
                                             id uuid primary key,
                                             user_id uuid not null references users(id),
    channel varchar(32) not null,
    destination varchar(512) not null,
    event_type varchar(128) not null
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

    retry_count int not null default 0,
    last_error varchar(1000),

    created_at timestamptz not null,
    updated_at timestamptz not null
    );

create index if not exists ix_notifications_user_created_at
    on notifications(user_id, created_at);

create index if not exists ix_notifications_status
    on notifications(status);
