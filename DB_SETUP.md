# Database Setup Instructions

Since you are using **Supabase** (PostgreSQL) in the cloud, Supabase provisions exactly **one** main database for you automatically called `postgres`. 

Because both the `AuthService` and `ExpenseService` will share this same Supabase instance, they will both connect to the `postgres` database.

However, Spring Boot (Hibernate) will automatically generate the required tables for this service when it boots up because we have set:
```properties
spring.jpa.hibernate.ddl-auto=update
```

### Local PostgreSQL Commands
If you ever want to run this service *locally* without Supabase, you will need to create the database manually. You can run these commands inside your local `psql` terminal:

```sql
-- Connect to postgres
psql -U postgres

-- Create the database
CREATE DATABASE authservice;

-- Connect to the new database
\c authservice;

-- (Optional) Create a specific user for this service
CREATE USER auth_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE authservice TO auth_user;
```

### Supabase Note
If you want to keep the tables for `AuthService` completely separate from `ExpenseService` inside Supabase, you can create a specific **Schema** instead. 

Run this in the Supabase SQL Editor:
```sql
CREATE SCHEMA auth_schema;
```

And then update your `.env` Database URL to point to that schema:
```env
DB_URL=jdbc:postgresql://${DB_HOST}:${DB_PORT:5432}/${DB_NAME:postgres}?currentSchema=auth_schema
```
