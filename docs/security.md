# Security

JWT authentication supports three roles:

| Role | Access |
| --- | --- |
| `USER` | user subscriptions, preferences, notification history |
| `OPERATOR` | delivery queue, failures, attempts, manual retry, dead-letter view |
| `ADMIN` | full access |

Demo credentials:

- `user/user`
- `operator/operator`
- `admin/admin`

## Audit Log

The `audit_logs` table records:

- subscription creation
- preferences updates
- manual retry/requeue
- RBAC-denied administrative attempts
- other security-sensitive workflows

## Safe Logging

Logs include request and correlation identifiers, delivery ids, retry attempt information, and actor id where applicable. Secrets such as JWT tokens, webhook secrets, Telegram bot tokens, and SMTP credentials are not logged.
