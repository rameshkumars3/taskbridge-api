# TaskBridge frontend

Angular client for the TaskBridge Spring Boot API.

## Run

From this directory:

```powershell
npm install
npm start
```

The API must be running on `http://localhost:8080`. The client uses `/api` paths; configure a proxy or host the client behind the API origin for deployments. The API accepts `http://localhost:4200` by default. Set `APP_WEB_ALLOWED_ORIGINS` to a comma-separated list of trusted origins when needed.

## Validate

```powershell
npm run build
npm test
```

The client reads `userId`, `organisationId`, and permissions from the signed JWT payload only to shape the UI and select the authenticated user's notification endpoint. Tenant and actor authority remain server-side responsibilities.
