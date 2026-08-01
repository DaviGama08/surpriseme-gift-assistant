# Security and privacy

## Local LLM configuration

The provider API key is loaded only when a real generation request is made. Configure one of the following for a local demonstration:

1. Copy `secrets.example.properties` to `secrets.properties` and set `LLM_API_KEY` locally; or
2. Define the `SURPRISEME_LLM_API_KEY` environment variable.

The environment variable takes precedence over the local file.

An API key shipped in a desktop client is extractable. Restrict and rotate demonstration credentials. A production deployment must keep the real provider credential behind a controlled backend rather than distributing it with the application.

`secrets.properties` is ignored by Git. Before committing, verify:

```powershell
git check-ignore -v secrets.properties
git status --short
```

Rotate any API key that was previously committed or shared, then update only the local secret source. Removing a key from the current tree does not revoke it or erase it from Git history.

## User data and external processing

- The interface asks for explicit consent before sending recipient criteria to the configured LLM provider.
- Logs do not include prompts, recipient profiles, provider responses or full local paths.
- Serialized user data is local and must not be committed or shared. Override its directory with `SURPRISEME_DATA_DIR` or the `surpriseme.data.dir` JVM property when isolation is required.
- Passwords are stored using salted PBKDF2-HMAC-SHA-256. Legacy SHA-3 or plaintext records are accepted only for a successful one-time migration to PBKDF2.

## Repository hygiene

Do not commit API keys, `secrets.properties`, serialized user data, certificates, IDE metadata or generated build files. CI and automated tests use an injected fake LLM client and require no provider secret.
