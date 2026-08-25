# Security and privacy

## Local secrets

`.env` is local only. It is ignored by Git and must never be committed.

The provider API key is loaded only when a real generation request is made. Configure one of the following for a local demonstration:

1. Define the `SURPRISEME_LLM_API_KEY` environment variable; or
2. Copy `.env.example` to `.env` and set `SURPRISEME_LLM_API_KEY` locally.

The environment variable takes precedence over `.env`. Endpoint and model fall back to safe non-secret defaults; the API key never has a default.

An API key shipped in a desktop client is extractable. Restrict and rotate demonstration credentials. A production deployment must keep the real provider credential behind a controlled backend rather than distributing it with the application.

If an API key is exposed, rotate it immediately with the provider and update only the local secret source. Removing a key from the current tree does not revoke it or erase it from Git history.

Before committing, verify:

```powershell
git check-ignore -v .env
git status --short
```

## Passwords

Passwords are stored using salted PBKDF2-HMAC-SHA-256. Older SHA-3 or plaintext records exist only so that a successful login can migrate them to PBKDF2. They are not a supported long-term format.

## User data and local files

- User data remains local (default directory `~/.surprise_me`). Do not share serialized files (`data.spm`, `.bak`, or staging `.new` / `.tmp`).
- Override the directory with `SURPRISEME_DATA_DIR` or the `surpriseme.data.dir` JVM property when isolation is required.
- Saves write a staging file and replace `data.spm` atomically. A readable primary is rotated to `data.spm.bak` before replacement; load recovers from staging or backup if the primary is unreadable.
- Java deserialization uses an `ObjectInputFilter` allow-list so unexpected types and oversized graphs are rejected.

## LLM processing

- Recipient criteria are sent to the configured LLM provider only after explicit consent in the UI.
- Logs do not include prompts, recipient profiles, provider responses or full local paths.
- The application starts without an API key. Suggestions that need a provider fail until one is configured.

## Repository hygiene

Do not commit API keys, `.env`, leftover `secrets.properties`, serialized user data, certificates, IDE metadata or generated build files. CI and automated tests use an injected fake LLM client and require no provider secret.
