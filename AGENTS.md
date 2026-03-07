# AGENTS

## Page Architecture Rule

- For any page-related code changes, read `PAGE_ARCHITECTURE_GUIDE.md` in the repository root before making edits.
- `Unit` must be instantiated and maintained inside `Model`; `Screen` can only collect/read it.
- Data passed through `Screen` parameters must go into `State`, not `Unit`.
