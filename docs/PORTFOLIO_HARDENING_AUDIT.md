# Portfolio hardening audit

## Scope

This audit records the changes made on `portfolio-hardening`. It does not rewrite Git history and does not claim that credentials exposed before this branch have been revoked.

## Baseline

- The project used custom Maven source and test directories.
- Local GitLab CI files, launch scripts and IDE/build artefacts obscured the portable build path.
- Passwords were transformed directly with unsalted SHA-3 in the interface.
- Tests could read or overwrite `~/.surprise_me/data.spm`.
- LLM calls had no explicit consent gate and tests depended on the concrete provider client.
- Baseline verification passed 198 tests.

## Corrections

- Adopted standard `src/main/java`, `src/main/resources` and `src/test/java` Maven layout.
- Removed repository-specific GitLab configuration and local launch scripts; added secret-free GitHub Actions validation.
- Centralized password handling in `PasswordHasher` using salted PBKDF2-HMAC-SHA-256 with constant-time comparison.
- Retained legacy SHA-3/plaintext verification only to migrate an existing account after a successful login.
- Isolated Maven tests under `target/test-data` and made the application data directory configurable.
- Introduced the `LlmClient` contract, lazy credential loading, configurable HTTPS provider settings and deterministic fake-client tests.
- Added an explicit user-consent gate before recipient criteria leave the application.
- Avoided logging prompts, personal data, response bodies, secrets or full local paths.
- Updated Jackson Databind to 2.17.3.

## Verification

Run from the repository root:

```text
mvn --batch-mode --no-transfer-progress clean verify
```

Result on 2026-08-01: build successful; 202 tests run, 0 failures, 0 errors and 0 skipped.

## Remaining operational actions

- Rotate/revoke every provider credential that was previously committed or shared. This cannot be completed in source code.
- If old secrets must be removed from Git history, coordinate a history rewrite separately with all collaborators.
- Do not distribute a real provider credential inside this desktop application. A production release should use a controlled backend and an application-specific authorization mechanism.
- Java serialization is retained for academic compatibility; sensitive production data should use authenticated encryption and a versioned storage format.
