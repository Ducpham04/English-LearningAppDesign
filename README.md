<h1 align="center">English Learning Platform</h1>

<p align="center">
  Online English learning for students, teachers and admins: video lessons, sentence-level pronunciation scoring, AI conversation practice, classes, assignments and progress tracking.
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white"/>
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white"/>
  <img src="https://img.shields.io/badge/React-18-61DAFB?style=for-the-badge&logo=react&logoColor=black"/>
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white"/>
  <img src="https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white"/>
</p>

## Features

- **Learning path** — Curriculum → Session → Lesson, with video, pronunciation, AI conversation, vocabulary and quiz lessons
- **Pronunciation scoring** — sentence-level scoring with Azure Speech
- **Auto transcripts** — transcripts for YouTube and uploaded lesson videos via AWS Transcribe
- **AI conversation** — in-lesson conversation practice powered by the OpenAI API
- **Classes & assignments** — teachers manage classes, assign work, grade and follow each student's progress
- **Admin** — users, topics, sessions, lessons, tests, video content and reports

## Repository layout

| Folder | What it is | Stack |
| --- | --- | --- |
| [`AppEnglish/`](AppEnglish) | REST API (Student / Teacher / Admin, JWT auth) | Java 21 · Spring Boot 3.5 · Spring Security · JPA · PostgreSQL · AWS S3 · Azure Speech · OpenAI |
| [`English Learning App Design/`](English%20Learning%20App%20Design) | Web frontend (SPA) | React 18 · TypeScript · Vite · Tailwind CSS |
| [`docs/`](docs) | Design notes | — |

## Run locally

**Backend**

```bash
cd AppEnglish
cp .env.example .env        # fill in your own database and API keys
docker compose up -d        # local database and services
./mvnw spring-boot:run      # API on http://localhost:8080
```

**Frontend**

```bash
cd "English Learning App Design"
cp .env.example .env        # point it at the API
npm install
npm run dev
```

Secrets are read from environment variables only; never commit a real `.env` file.

## More documentation

- Backend details: [`AppEnglish/README.md`](AppEnglish/README.md)
- Frontend details: [`English Learning App Design/README.md`](English%20Learning%20App%20Design/README.md)
- Project report (Vietnamese): [`BAO_CAO_TONG_HOP_DU_AN.md`](BAO_CAO_TONG_HOP_DU_AN.md)
