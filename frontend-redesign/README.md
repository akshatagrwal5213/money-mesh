# MoneyMesh Frontend Redesign

This is a minimal Vite + React frontend to replace the existing frontend.

## Run

```bash
cd frontend-redesign
npm install
npm run dev
```

The dev server runs on port 5174 and proxies `/api` to `http://localhost:8080`.

## Pages

- `/login` - simple login form that stores JWT and redirects to `/customers`
- `/customers` - minimal customers list using `/api/customers` (requires login token)

