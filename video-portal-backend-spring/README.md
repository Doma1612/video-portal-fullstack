# video-portal-backend-spring

Clean Spring Boot backend scaffold for the video portal.

## Run (dev)

```bash
mvn spring-boot:run
```

By default this uses an embedded H2 database (file-based) and stores uploaded videos under `./data/`.

## Run (Docker)

The repository root contains a `docker-compose.yml` which starts:

- Postgres
- Spring Boot backend (this module)
- Nginx container serving the frontend and proxying `/api/*` to the backend

From the repository root:

```bash
docker compose up --build
```

## Configuration

See `src/main/resources/application.yml` for:

- `app.storage.initial-upload-dir`
- `app.storage.converted-dir`
- `app.ffmpeg.path`

If `ffmpeg` is not installed or `app.ffmpeg.path` is wrong, upload will fail.

## API

REST endpoints (v1):

- Auth
	- `POST /api/v1/auth/login`
	- `POST /api/v1/auth/register`
- Themes
	- `GET /api/v1/themes`
	- `POST /api/v1/themes`
	- `PUT /api/v1/themes/{id}`
	- `DELETE /api/v1/themes/{id}`
- Subcategories
	- `GET /api/v1/subcategories`
	- `POST /api/v1/subcategories`
	- `PUT /api/v1/subcategories/{id}`
	- `DELETE /api/v1/subcategories/{id}`
- Videos
	- `GET /api/v1/videos` (optional query: `?q=...`)
	- `POST /api/v1/videos` (`multipart/form-data`)
	- `GET /api/v1/videos/{id}`
	- `GET /api/v1/videos/{id}/stream` (supports HTTP Range)
