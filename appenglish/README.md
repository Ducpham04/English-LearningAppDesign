# English AI Learning Platform

A comprehensive English learning platform built with Spring Boot, featuring AI-powered conversation, face verification, gamification, and multi-role user management.

## Features

### Core Learning Features
- **Curriculum Management**: Structured learning paths with topics and lessons
- **Interactive Quizzes**: Multiple choice and practice quizzes with scoring
- **AI Pronunciation Analysis**: Real-time speech analysis and feedback
- **AI Conversation Practice**: Interactive conversation sessions with AI tutor

### Assessment & Evaluation
- **Comprehensive Exams**: Full assessment system separate from quizzes
- **Face Verification**: Secure exam taking with face ID verification
- **Progress Tracking**: Detailed learning analytics and progress monitoring

### User Management
- **Multi-Role System**: Student, Teacher, and Admin roles
- **Class Management**: Teachers can create and manage classes
- **Enrollment System**: Students can join multiple classes

### Gamification
- **Daily Missions**: Achievement system with rewards
- **Streaks**: Learning streak tracking and maintenance
- **Leaderboards**: Competitive learning environment

### Security & Audit
- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access Control**: Granular permissions system
- **Audit Logging**: Comprehensive activity and change tracking

## Technology Stack

- **Backend**: Spring Boot 3.5.9, Java 21
- **Database**: MySQL 8.0 with JPA/Hibernate
- **Security**: Spring Security with JWT
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven

## Prerequisites

- Java 21 or higher
- Docker and Docker Compose
- Maven 3.6+

## Quick Start

### Using Docker Compose (Recommended)

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd AppEnglish
   ```

2. **Start the application**
   ```bash
   docker-compose up --build
   ```

3. **Access the application**
   - API: http://localhost:8080/api
   - MySQL: localhost:3306

### Local Development

1. **Start MySQL database**
   ```bash
   docker-compose up mysql
   ```

2. **Run the application**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **Access the application**
   - API: http://localhost:8080/api

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Learning Content (Student)
- `GET /api/learning/topics` - Get available topics
- `GET /api/learning/lessons/{id}` - Get lesson details
- `POST /api/learning/quizzes/{id}/attempt` - Start quiz attempt

### Class Management (Teacher)
- `POST /api/teacher/classes` - Create new class
- `GET /api/teacher/classes/{id}/students` - Get class students
- `POST /api/teacher/classes/{id}/enroll` - Enroll student

### Admin Functions
- `GET /api/admin/users` - User management
- `GET /api/admin/audit-logs` - System audit logs
- `POST /api/admin/system-settings` - System configuration

## Database Schema

The application uses a comprehensive MySQL database with the following main entities:

- **Users & Roles**: User management with role-based permissions
- **Learning Content**: Topics, Lessons, Quizzes, and Questions
- **Progress Tracking**: User progress, quiz attempts, speaking practice
- **Class Management**: Classes, enrollments, and teacher assignments
- **Assessment**: Exams, exam attempts, and scoring
- **AI Features**: Conversation sessions and messages
- **Security**: Face profiles, verification logs, audit trails
- **Gamification**: Missions, streaks, and achievements

## Configuration

### Application Properties
- Database connection settings
- JWT token configuration
- Security settings
- File upload limits

### Environment Variables
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/english_learning
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
APP_JWT_SECRET=your_jwt_secret_key
```

## Development

### Running Tests
```bash
./mvnw test
```

### Building for Production
```bash
./mvnw clean package
```

### Code Style
- Follow standard Java naming conventions
- Use Lombok for boilerplate code reduction
- Implement proper error handling and validation

## Deployment

### Production Deployment
1. Update `application-prod.properties` with production settings
2. Build the application: `./mvnw clean package -DskipTests`
3. Use the provided Dockerfile for containerization
4. Configure reverse proxy (nginx) for SSL termination

### Database Migration
The application uses JPA auto-ddl with `update` strategy for development.
For production, consider using Flyway or Liquibase for proper database versioning.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.