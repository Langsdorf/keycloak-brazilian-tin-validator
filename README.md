# Keycloak Brazilian TIN Validator

A Keycloak extension that validates Brazilian Tax Identification Numbers (TIN), including CPF (Cadastro de Pessoas Físicas) and CNPJ (Cadastro Nacional da Pessoa Jurídica).

## Features

- Validates CPF (Individual Taxpayer Registry) format and check digits
- Validates CNPJ (National Registry of Legal Entities) format and check digits
- Custom validator for Keycloak user registration and profile forms
- Configurable error messages

## Installation

1. Build the project:
```bash
mvn clean package
```

2. Copy the generated JAR file to your Keycloak deployment:
```bash
cp target/keycloak-brazilian-tin-validator-*.jar /path/to/keycloak/providers/
```

3. Restart Keycloak to load the new provider.

## Usage

Add the validator to your user profile configuration or registration form in the Keycloak admin console.

## Requirements

- Keycloak 21.0.0 or higher
- Java 11 or higher

## License

This project is licensed under the Apache License 2.0.