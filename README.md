# IELTS Beta

IELTS Beta is a web-based IELTS Learning Management System (LMS) built for IELTS preparation. The project provides a student learning platform with practice, tests, results, and learning resources.

## Tech Stack

* Next.js
* React
* TypeScript
* Tailwind CSS
* Supabase
* PostgreSQL
* Spring Boot

## Project Structure

```text
IELTS-Beta/
├── frontend/       # Next.js frontend application
├── backend/        # Spring Boot backend
├── supabase/       # Database schema and SQL files
└── README.md
```

## Prerequisites

Before running the project, make sure you have installed:

* Node.js 18+
* npm
* Java 17+
* Maven
* Git
* A Supabase project

## 1. Clone the Repository

```bash
git clone https://github.com/YOUR_USERNAME/IELTS-Beta.git
cd IELTS-Beta
```

## 2. Frontend Setup

Go to the frontend folder:

```bash
cd frontend
```

Install dependencies:

```bash
npm install
```

Create the environment file:

```bash
cp .env.local.example .env.local
```

For Windows PowerShell:

```powershell
Copy-Item .env.local.example .env.local
```

Open `.env.local` and add your Supabase configuration:

```env
NEXT_PUBLIC_SUPABASE_URL=your_supabase_url
NEXT_PUBLIC_SUPABASE_ANON_KEY=your_supabase_anon_key
```

Start the frontend:

```bash
npm run dev
```

The frontend will run at:

```text
http://localhost:3000
```

## 3. Backend Setup

Open a new terminal and go to:

```bash
cd IELTS-Beta/backend
```

Configure the required environment variables according to the backend `.env.example` file.

Run the Spring Boot application:

```bash
mvn spring-boot:run
```

The backend will run at:

```text
http://localhost:8080
```

## 4. Supabase Database Setup

Open your Supabase project.

Run the SQL files from the `supabase` folder in the required order:

```text
supabase/
├── schema.sql
├── seed.sql
├── storage-setup.sql
└── bootstrap_admin.sql
```

These files create and populate the required database tables, policies, storage configuration, and initial admin setup.

## 5. Run the Project

After completing the frontend, backend, and Supabase configuration:

### Frontend

```bash
cd frontend
npm run dev
```

### Backend

```bash
cd backend
mvn spring-boot:run
```

Then open:

```text
http://localhost:3000
```

## Environment Variables

Do **not** commit `.env.local` or other files containing secrets.

Use the provided example files:

```text
frontend/.env.local.example
backend/.env.example
```

Add your own Supabase and backend configuration locally.

## Notes

This project is intended as an IELTS LMS MVP/demo application. The frontend and backend are maintained in the same repository for easier development and deployment.
