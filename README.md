## [Máster en Ingeniería Web por la Universidad Politécnica de Madrid (miw-upm)](http://miw.etsisi.upm.es)

## Back-end con Tecnologías de Código Abierto (BETCA).

> Aplicación TPV. Pretende ser un ejemplo práctico y real de todos los conocimientos vistos

## Estado del código

[![Spring Core - Tests](https://github.com/miw-upm/betca-tpv-core/actions/workflows/ci.yml/badge.svg)](https://github.com/miw-upm/betca-tpv-core/actions/workflows/ci.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?branch=develop&project=es.upm.miw%3Abetca-tpv-core&metric=alert_status)](https://sonarcloud.io/dashboard?id=es.upm.miw%3Abetca-tpv-core&branch=develop)

### Tecnologías necesarias

`Java` `Maven` `GitHub` `Spring-boot` `Sonarcloud` `MongoDB` `Docker` `Render`

## :gear: Ejecución en local

1. Arrancar Docker Desktop
1. Ejecutar en consola: `docker compose up --build -d`

* Cliente Web (OpenAPI): `http://localhost:8082/swagger-ui.html`
* Ver los logs (con -f se queda escuchando, Ctrl+C para salir): `docker logs [-f] core-api`
* Para parar: `docker compose stop`, aunque resulta mas práctico manejar los contenedores desde Docker Desktop
* Arrancar la consola de MongoDB sobre la BD:
  `docker exec -it mongo-db mongosh "mongodb://mongo:mongo@localhost:27017/tpv?authSource=admin"`

## :book: Documentación del proyecto

[betca-tpv: Core](https://github.com/miw-upm/betca-tpv#back-end-core).
