[![Build Status](https://github.com/edigonzales/wmts-avdpool/workflows/CI/CD/badge.svg)](https://github.com/edigonzales/wmts-avdpool/workflows/CI/CD/badge.svg)

# wmts-avdpool

## Developing
```
docker run --rm --name wmts-db -p 54321:5432 --hostname primary \
-e PG_DATABASE=wmts -e PG_LOCALE=de_CH.UTF-8 -e PG_PRIMARY_PORT=5432 -e PG_MODE=primary \
-e PG_USER=admin -e PG_PASSWORD=admin \
-e PG_PRIMARY_USER=repl -e PG_PRIMARY_PASSWORD=repl \
-e PG_ROOT_PASSWORD=secret \
-e PG_WRITE_USER=gretl -e PG_WRITE_PASSWORD=gretl \
-e PG_READ_USER=ogc_server -e PG_READ_PASSWORD=ogc_server \
-v ~/pgdata-wmts:/pgdata \
sogis/wmts-db:latest
```

```
docker run --rm --name wmts-db -p 54321:5432 --hostname primary \
-e PG_DATABASE=wmts -e PG_LOCALE=de_CH.UTF-8 -e PG_PRIMARY_PORT=5432 -e PG_MODE=primary \
-e PG_USER=admin -e PG_PASSWORD=admin \
-e PG_PRIMARY_USER=repl -e PG_PRIMARY_PASSWORD=repl \
-e PG_ROOT_PASSWORD=secret \
-e PG_WRITE_USER=gretl -e PG_WRITE_PASSWORD=gretl \
-e PG_READ_USER=ogc_server -e PG_READ_PASSWORD=ogc_server \
sogis/wmts-db:latest
```

## TODO
- ENV-Vars anpassen an `/camel`. Nur in `wmts-stack`?