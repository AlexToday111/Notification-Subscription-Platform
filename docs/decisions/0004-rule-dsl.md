# 0004 Rule DSL

Subscriptions need expressive filtering without compiling arbitrary code or adding a scripting engine.

Decision: store a small JSON DSL in `subscriptions.condition_json`.

The DSL supports `all`, `any`, path-based field lookup, and simple operators: `eq`, `ne`, `gt`, `gte`, `lt`, `lte`, `in`, and `exists`.

This keeps the model auditable, serializable, testable, and safe for API usage.
