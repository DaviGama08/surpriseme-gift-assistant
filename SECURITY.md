# Local LLM configuration

The Groq API key is never committed. Configure one of the following before running the application:

1. Copy `secrets.example.properties` to `secrets.properties` and set `LLM_API_KEY` locally; or
2. Define the `SURPRISEME_LLM_API_KEY` environment variable.

The environment variable takes precedence over the local file.

`secrets.properties` is ignored by Git. Before committing, verify:

```powershell
git check-ignore -v secrets.properties
git status --short
```

Rotate any API key that was previously committed or shared, then update only the local secret source. Do not commit keys, serialized user data, certificates, or generated build files.
