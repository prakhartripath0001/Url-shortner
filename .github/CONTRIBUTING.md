# Contributing Guidelines

Thank you for considering contributing to Url-shortner! To maintain code quality, consistency, and clean git history, please follow these guidelines.

## Branching Strategy
- Main branch is `main`.
- Create feature or bug fix branches using descriptive names:
  - `feature/short-description`
  - `bugfix/short-description`
  - `refactor/short-description`

## Workflow Steps
1. Fork the repository and create your branch from `main`.
2. Implement your changes.
3. Ensure the project builds successfully and passes all existing tests.
4. Run locally to verify functionality.
5. Commit your changes using clear commit messages.
6. Push to your fork and submit a Pull Request.

## Pull Request Rules
- Provide a clear, detailed description of the changes in the Pull Request description using our template.
- Link the Pull Request to any related issues.
- All code modifications must compile without warnings or errors.
- Ensure Lombok builders and entities are correctly annotated to avoid default-value overriding issues.
- Do not use emojis in commit messages or pull request descriptions.
- Request review from repository maintainers.

## Code Style & Standards
- Keep package structures cohesive.
- Always use FetchType.LAZY for ManyToOne/OneToMany JPA mappings to avoid unnecessary database queries.
- Ensure variables and methods are named clearly using camelCase format.
- Ensure classes and interfaces use PascalCase format.
