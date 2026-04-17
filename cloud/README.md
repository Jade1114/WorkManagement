# WorkManagement Cloud Skeleton

This directory contains a minimal Spring Cloud learning setup.

Modules:
- `gateway-service`: API gateway, service discovery entry point, no database dependency.
- `user-service`: user domain placeholder with Web, Validation, JPA, and MySQL starters.
- `education-service`: education domain placeholder with Web, Validation, JPA, MySQL, and OpenFeign.

Suggested startup order:
1. Start Nacos at `localhost:8848`.
2. Start `user-service`.
3. Start `education-service`.
4. Start `gateway-service`.

Notes:
- `user-service` and `education-service` already include datasource properties, but database auto-configuration is temporarily excluded so the empty skeleton can start before real entities and repositories are added.
- If you want to verify startup without a local Nacos instance, run with `NACOS_DISCOVERY_ENABLED=false`.
