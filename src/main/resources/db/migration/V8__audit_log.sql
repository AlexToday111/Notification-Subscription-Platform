create table if not exists audit_logs (
    id uuid primary key,
    actor_user_id varchar(160),
    action varchar(120) not null,
    entity_type varchar(80) not null,
    entity_id varchar(120),
    details text,
    created_at timestamptz not null,
    correlation_id varchar(120)
);

create index if not exists ix_audit_logs_created_at
    on audit_logs(created_at);

create index if not exists ix_audit_logs_action_created_at
    on audit_logs(action, created_at);
