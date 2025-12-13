# Cinema Booking System

A hybrid web application for cinema management and ticket booking. This project combines a **Spring Boot** backend with a dual frontend strategy: **Thymeleaf** for server-side rendering (SEO/Admin) and **Next.js** for interactive client-side operations (Booking).

## 🚀 Key Features

- **Hybrid Architecture:**
  - **Public/Admin Portal:** Built with Thymeleaf & Bootstrap 5 (SEO-friendly, server-side rendered).
  - **Booking Widget:** Built with Next.js (Interactive seat selection, dynamic cart).
- **Core Functionalities:**
  - Movie Repertoire & Search.
  - Interactive Cinema Hall (Visual seat selection).
  - Ticket Booking with various pricing types (Normal/Reduced/Family).
  - Admin Panel: CRUD for Movies, Screenings, and Rooms.
  - Sales Reporting & Analytics.
- **Technical Highlights:**
  - Dual Data Access: usage of both **Spring Data JPA** and **JdbcTemplate**.
  - Spring Security integration (Form Login & API Security).
  - File Uploads & PDF Export.
  - OpenAPI / Swagger Documentation.

## 🛠️ Tech Stack

### Backend

- **Java 21+**
- **Spring Boot 3.x** (Web, Data JPA, Security, Validation)
- **Database:** H2 (In-memory for Dev) / PostgreSQL (Prod)
- **Tools:** Lombok, Maven, OpenPDF

### Frontend

- **Module A (SSR):** Thymeleaf, Bootstrap 5
- **Module B (SPA):** Next.js, React, Axios

---

## ⚙️ Getting Started

### Prerequisites

- JDK 21 or higher
- Node.js & npm (for the Next.js module)
- Maven

# 📂 Project Structure

```
root/
├── backend-app/       # Spring Boot Monolith (Core + Thymeleaf Views)
│   ├── src/main/java  # Controllers, Services, Entities
│   └── src/main/resources/templates # Thymeleaf HTML files
├── frontend-client/   # Next.js Application (Booking Process)
└── docker-compose.yml # Optional database setup
```
