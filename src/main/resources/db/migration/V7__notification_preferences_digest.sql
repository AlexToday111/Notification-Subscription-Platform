create table if not exists notification_preferences (
    user_id uuid primary key references users(id) on delete cascade,
    allowed_channels varchar(256) not null,
    preferred_channel varchar(32),
    quiet_hours_start time,
    quiet_hours_end time,
    timezone varchar(80) not null,
    digest_mode varchar(32) not null,
    updated_at timestamptz not null
);

create table if not exists notification_digest_items (
    id uuid primary key,
    user_id uuid not null references users(id) on delete cascade,
    notification_id uuid not null references notifications(id) on delete cascade,
    digest_mode varchar(32) not null,
    available_at timestamptz not null,
    status varchar(32) not null,
    created_at timestamptz not null
);

create index if not exists ix_notification_digest_items_status_available
    on notification_digest_items(status, available_at);
