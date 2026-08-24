# SurpriseMe Gift Assistant

> **Academic Project — Instituto Superior de Engenharia de Coimbra (ISEC)**
>
> This public repository is a portfolio-ready version. The original academic submission is preserved separately in a private `-isec-archive` repository; later improvements may be present here.

## Overview

SurpriseMe Gift Assistant is a JavaFX desktop application for organising recipients, occasions and gift history and for producing personalised gift and card-message suggestions.

It was developed collaboratively at ISEC as an academic software-engineering project. This repository is presented for technical and portfolio review, not as a commercial product.

## Main Features

- User registration, authentication and profile management
- Recipient profiles with interests, preferences and relationship details
- Upcoming-event creation, editing and chronological display
- Gift-history tracking and feedback
- Personalised and spontaneous gift suggestions
- Gift-card message suggestions
- Local persistence across application sessions
- JavaFX desktop interface
- Automated tests for core domain behaviour

## Architecture

The application separates its main responsibilities into:

| Area | Responsibility |
| --- | --- |
| JavaFX UI | Screens, navigation and user interaction |
| Domain model | Users, recipients, events, gifts and application rules |
| Application services | Coordinates workflows between the UI and model |
| Persistence | Stores and restores local application data |
| Suggestion adapter | Isolates the optional external recommendation integration |

The repository also contains UML diagrams, user stories and incremental planning artifacts from the academic development process.

## Technologies

- Java 21
- JavaFX 21
- Apache Maven
- JUnit 5
- Jackson
- cron-utils
- GitLab CI and SonarCloud configuration

## Run Locally

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

The optional suggestion integration uses local configuration based on `.env.example`. Copy the example to `.env` and replace placeholders locally; never commit real credentials.

## Engineering Concepts Demonstrated

- Object-oriented domain modelling
- Separation of UI, model and integration concerns
- Desktop application development with JavaFX
- Local persistence
- External-service abstraction
- Automated testing
- Iterative delivery using user stories and acceptance criteria
- Collaborative version-control workflows

## Team

- Celso André Ferreira Jordão
- Davi Nasser Torres Gama — [@DaviGama08](https://github.com/DaviGama08)
- Hugo Rafael da Assunção Gomes
- Rita Mariana Alves Henriques
- Rui Manuel Borges Casaca

Student numbers and academic email addresses are intentionally omitted from this public portfolio version.

## Licence

No open-source licence has been assigned. The repository is available for portfolio and educational review; reuse or redistribution requires permission from the authors.
