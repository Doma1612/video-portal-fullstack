# Video Platform (Docker)

## Start everything (one command)

```bash
docker compose up --build
```

- Frontend: http://localhost:3000
- Backend (direct): http://localhost:8080/api/v1

## Stop

```bash
docker compose down
```

## Data

Docker volumes:

- `db_data` (Postgres)
- `video_data` (uploaded + converted videos)
