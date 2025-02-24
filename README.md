# Odysejapka

Odyssey of the Mind Poland tournament application.

## API Docs

Generated automatically with Swagger. You can find them under:
``/swagger-ui/index.html#/``

## How to run

### IntelliJ

For IntelliJ there is run configuration called `Odysejapka` just run it and it will execute docker db, ui, backend for
you.

### Other
1. Run `docker-compose up -d` for database
2. Build and run **OdysejapkaApplication.kt**
3. Move to `odyseja-ui` and run `npm run dev`

### Google API credentials

To run services like SAK, GAD, TM, Fixer you need to provide `GOOGLE_APPLICATION_CREDENTIALS`.

To do that set `GOOGLE_APPLICATION_CREDENTIALS` to your `credentials.json` location.

To get `credentials.json` contact maintainer of this application.

