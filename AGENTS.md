# AGENTS

## Page Architecture Rule

- For any page-related code changes, read `PAGE_ARCHITECTURE_GUIDE.md` in the repository root before making edits.
- `Unit` must be instantiated and maintained inside `Model`; `Screen` can only collect/read it.
- Data passed through `Screen` parameters must go into `State`, not `Unit`.

## Screen Restore Rule

- Any `Screen` with constructor parameters must implement `Parcelable` (typically `@Parcelize`) to support process/background restore.
- Parameter models passed into a `Screen` must also be `Parcelable`.
- Do not pass non-serializable callbacks/lambdas in `Screen` params; use navigator result/effect/state channels instead.
- If a callback must temporarily stay in params, mark it `@IgnoredOnParcel` and ensure restore-safe fallback behavior is defined.
