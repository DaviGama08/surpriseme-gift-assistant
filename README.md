# SurpriseMe Gift Assistant

SurpriseMe is a JavaFX desktop application for organising gift recipients, occasions and gift history, and for producing personalised gift and card-message suggestions.

It began as an ISEC (Instituto Superior de Engenharia de Coimbra) group project. This public repository contains later portfolio hardening; it is not a commercial product and does not ship a real API key.

## Screens and major features

- Login and registration
- Dashboard of upcoming occasions
- Recipient ("enjoyer") profiles with interests, preferences and relationship details
- Event creation, editing and chronological display
- Gift history, status and feedback
- Personalised and spontaneous gift suggestions
- Gift-card message suggestions
- User profile management
- Local persistence across sessions

## Architecture

The code is organised around these packages under `pt.isec.gps2526_g42.surprise_me`:

| Package | Responsibility |
| --- | --- |
| `ui` | JavaFX screens, navigation and dialogs |
| `application` | Coordinates workflows between the UI and the rest of the system |
| `model` | Users, recipients, events, gifts and domain rules |
| `persistence` | Serialises and restores local application data |
| `security` | Password hashing and verification |
| `config` | Environment, `.env` and non-secret defaults |
| `integration.llm` | Optional Groq-compatible suggestion client |

## Technologies

- Java 21
- JavaFX 21
- Apache Maven
- JUnit 5
- Jackson
- cron-utils
- dotenv-java

## Security and privacy

- User data is stored locally (default directory `~/.surprise_me`).
- Passwords are hashed with salted PBKDF2-HMAC-SHA-256.
- Secrets are not committed. `.env` is Git-ignored; copy `.env.example` locally.
- Recipient data is sent to an external LLM provider only after explicit consent in the UI.
- A real API key is not included in this repository.

See [SECURITY.md](SECURITY.md) for details.

## LLM integration

Gift and message suggestions can use an optional Groq-compatible HTTPS provider. The application starts without a key. Suggestions that need a provider fail until `SURPRISEME_LLM_API_KEY` is configured locally. Tests inject a fake client and do not call the network.

## Running locally

Requirements:

- JDK 21
- Apache Maven 3.9 or newer

Clone and enter the repository:

    git clone https://github.com/DaviGama08/surpriseme-gift-assistant-isec.git
    cd surpriseme-gift-assistant-isec

Run the test suite:

    mvn clean test

Start the desktop application:

    mvn javafx:run

## Configuration

Copy `.env.example` to `.env` and fill in local values. Resolution order is environment variable, then `.env`, then non-secret defaults. Secrets never have a default.

| Variable | Purpose |
| --- | --- |
| `SURPRISEME_LLM_API_KEY` | Provider key (optional; never defaulted) |
| `SURPRISEME_LLM_ENDPOINT` | HTTPS chat-completions endpoint |
| `SURPRISEME_LLM_MODEL` | Model name |
| `SURPRISEME_DATA_DIR` | Local data directory |

The data directory can also be set with the `surpriseme.data.dir` JVM property, which takes precedence over the environment variable.

## Testing

JUnit 5 covers domain, persistence, password hashing, configuration, consent and suggestion prompts. Maven Surefire isolates test data under `target/test-data`. CI runs `mvn clean verify` on Ubuntu and Windows without provider secrets.

    mvn --batch-mode --no-transfer-progress clean verify

## Academic origin and team

This began as an ISEC software-engineering group project. The original academic submission is preserved separately; the public repository contains later portfolio hardening.

- Celso André Ferreira Jordão
- Davi Nasser Torres Gama — [@DaviGama08](https://github.com/DaviGama08)
- Hugo Rafael da Assunção Gomes
- Rita Mariana Alves Henriques
- Rui Manuel Borges Casaca

Student numbers and academic email addresses are intentionally omitted from this public portfolio version.

## Licence

No open-source licence has been assigned. The repository is available for portfolio and educational review; reuse or redistribution requires permission from the authors.
