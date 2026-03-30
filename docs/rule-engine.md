# Rule Engine

Subscriptions can include `conditionJson`. Empty conditions match every payload for the subscription event type.

Example:

```json
{
  "all": [
    { "field": "severity", "op": "eq", "value": "CRITICAL" },
    { "field": "service", "op": "in", "value": ["billing", "auth"] },
    { "field": "payload.order.amount", "op": "gte", "value": 100 }
  ]
}
```

Supported operators:

- `eq`, `ne`
- `gt`, `gte`, `lt`, `lte`
- `in`
- `exists`

`payload.` is optional for path lookup, so `payload.user.id` and `user.id` both read the JSON path under the event payload root.

Edge cases:

- Missing path returns `false`, except `exists`, which returns `false` for missing/null.
- Invalid JSON or unsupported operators fail validation on subscription creation.
- If a stored rule somehow becomes invalid, evaluation returns `false` rather than failing event processing.
