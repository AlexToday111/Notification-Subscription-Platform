alter table subscriptions
    add column if not exists condition_json text;

alter table subscriptions
    add column if not exists updated_at timestamptz not null default now();
