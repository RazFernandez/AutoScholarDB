# 📚 AutoScholarDB: Automated Researcher Data Pipeline

## 🧭 Table of Contents
- [📌 Project Overview](#-project-overview)
- [🎯 Vision](#-vision)
- [💡 Proposed Solution](#-proposed-solution)
- [✅ Expected Impact](#-expected-impact)
- [⚙️ Key Architectural Decisions](#-key-architectural-decisions)
- [🏗️ Project Structure](#-project-structure)
- [📝 Documentation & Assets](#-documentation--assets)

---

## 📌 Project Overview

**Client:** Innovation Center, University in Northern Mexico  

At the university, the process of collecting and integrating information about the institution’s top researchers is still handled manually.  
Sandra, responsible for the research database, must continuously receive raw information that requires formatting and validation before integration.  

This creates inefficiencies, delays, and increases the likelihood of errors. Without automation, the Innovation Center cannot quickly analyze and share research metrics, which limits the institution’s ability to highlight achievements and support decision-making.

---

## 🎯 Vision

The project envisions an automated and reliable integration pipeline that retrieves data directly from the Google Scholar API, processes it into structured information, and stores it in the university’s research database.

By doing so, the Innovation Center will:
- Streamline the management of research information.  
- Strengthen transparency.  
- Showcase the institution’s most influential researchers more effectively.  

---

## 💡 Proposed Solution

The solution consists of a **three-phase development process** led by **Elizabeth (programmer)**, with **Renata (project leader)** and **Sandra (database manager)** as collaborators.

### 📍 Phase 1 (Sprint 1)
- Analyze and document the Google Scholar API.  
- Create a technical report.  
- Set up a GitHub repository with all documentation.  

### 📍 Phase 2 (Sprint 2)
- Develop a Java application using the MVC design pattern.  
- Perform GET requests to the Google Scholar Author API.  
- Temporarily store researcher data in memory.  
- Publish deliverables to GitHub.  

### 📍 Phase 3 (Sprint 3)
- Design a database schema.  
- Integrate the retrieved information (2 researchers, 3 articles each) into the research database.  
- Implement robust error handling.  
- Finalize documentation and version control in GitHub.  

---

## ✅ Expected Impact

This solution not only improves efficiency but also enhances collaboration by:
- Maintaining version control and documentation.  
- Ensuring the database receives accurate and timely updates from Google Scholar.  
- Supporting the Innovation Center’s mission of advancing research visibility and institutional decision-making.  

---

## ⚙️ Key Architectural Decisions

The following decisions were made to prioritize **rapid development**, **code maintainability**, and **security** within the constraints of the project timeline.

### 1. Spring Boot for Application Framework
**Decision:** Utilizing the Spring Boot framework.  
**Justification:** Spring Boot dramatically accelerates Rapid Application Development (RAD) by enforcing convention over configuration. Instead of manually setting up core components, the framework automates dependency injection and provides an embedded server (Tomcat), allowing the development team to focus entirely on implementing business logic and API integration.

---

### 2. GSON for JSON Processing
**Decision:** Choosing GSON over Jackson for JSON serialization/deserialization.  
**Justification:** For a project of this scale, GSON offers a cleaner, less verbose API compared to Jackson. GSON is highly effective for directly mapping JSON structures to Java DTOs (Data Transfer Objects), simplifying the data transformation step when fetching information from the Google Scholar API.

---

### 3. MVC Architecture Pattern
**Decision:** Implementing the Model-View-Controller (MVC) architectural pattern.  
**Justification:** The MVC pattern provides a clear separation of concerns, adhering to SOLID principles. This modularity ensures:
- **Maintainability:** Changes to the database access (Repository) don't affect the API endpoints (Controller).  
- **Testability:** Business logic (Service) can be tested in isolation.  
- **Readability:** The project structure is easy for new developers to onboard.  

---

### 4. PostgreSQL for Database Management
**Decision:** Selecting PostgreSQL as the relational database management system.  
**Justification:** PostgreSQL is a powerful, open-source object-relational database known for its stability and advanced features. The team’s existing experience enables quicker schema design and effective troubleshooting using tools like pgAdmin 4.

---

### 5. Hiding Credentials (API Keys and Database Details)
**Decision:** Storing all sensitive data (SerpAPI key and PostgreSQL credentials) in the `application.properties` file.  
**Justification:** This ensures that confidential information is never hardcoded or exposed in version control. Environment properties maintain security and align with professional deployment practices.

---

## 🏗️ Project Structure

This project follows the **Model-View-Controller (MVC)** pattern layered over a **Spring Boot** application. The structure separates business logic, data access, and third-party integration for modularity and maintainability.

### `src/main/java/com/autoscholardb/demo`

| Directory | Role | Description |
|------------|------|-------------|
| `model` | Data/DTOs | Contains Data Transfer Objects (DTOs) like `AuthorInfo` and `Article`, and Entities (e.g., `ArticleEntity`) for direct persistence mapping to the PostgreSQL database. |
| `controller` | Web Layer (API) | Defines REST API endpoints (e.g., `/api/scholar` and `/db/save`) that allow client interaction with backend services. |
| `services` | Business Logic | Holds core business logic and manages integration between APIs (`ScholarService`) and database (`ArticleDatabaseService`). |
| `repository` | Data Access | Contains Spring Data JPA interfaces (`ArticleRepository`) responsible for CRUD operations against PostgreSQL. |

---

### `src/main/resources`

| Directory | Role | Description |
|------------|------|-------------|
| `application.properties` | Configuration | Contains PostgreSQL connection details and Spring/Hibernate configurations. |
| `static/css` | Web Assets | Stores CSS files (`style.css`) for UI styling. |
| `static/js` | Client Logic | Contains JavaScript (`displayArticlesInfo.js`) for user interaction, API calls, and dynamic table rendering. |
| `static/views` | User Interface | Contains HTML (`index.html`) providing the browser UI framework. |

---

## 📝 Documentation & Assets

| Document | Description | Status |
|-----------|--------------|---------|
| **API Design** | Detailed specification of REST endpoints and request/response models. | [Link](https://github.com/RazFernandez/AutoScholarDB/blob/main/Docs/AutoScholarDB_API_Design.md) |
| **Database Model** | Entity Relationship Diagram (ERD) showing the `scholarly_articles` table and relationships. | [Link](https://github.com/RazFernandez/AutoScholarDB/blob/main/Docs/database-model.md) |
| **User Diagram** | Visual diagram to respent the actors and main features on the system | [Link](https://github.com/RazFernandez/AutoScholarDB/blob/main/Docs/user-case-diagram.md)|
| **User manual** | Manual for new user who use this webpage. | [Link](https://github.com/RazFernandez/AutoScholarDB/blob/main/Docs/user-manual.md) |
| **Budget** | Financial breakdown and resource allocation for the project. | [Link](https://docs.google.com/spreadsheets/d/10F8Z9fK3ypQaWO7WCvrfODMM9xwwthD7Xjip0uGMlGs/edit?gid=2146888974#gid=2146888974) |

---

## 🌱 Sustainability & Long-Term Benefits

The AutoScholarDB project is designed with sustainability and long-term maintainability in mind. Its automated pipeline and modular architecture ensure that the system remains useful and efficient over time. Key factors include:

- **Resource Efficiency:** By automating data collection and integration, the system reduces manual labor and the time required for staff to gather, validate, and input researcher data.
- **Maintainable Architecture:** The MVC pattern and separation of concerns allow future developers to easily modify, extend, or replace components without disrupting the entire system.
- **Scalability:** The use of PostgreSQL and Spring Boot allows the system to scale as the number of researchers, articles, and requests grows.
- **Data Integrity & Accuracy:** Automation minimizes human error, ensuring the research database remains reliable, which is critical for institutional decision-making.
- **Environmental Considerations:** Reduced paper usage and minimized repetitive manual tasks lower the operational carbon footprint, promoting a more sustainable research management process.
- **Open Documentation & Version Control:** Thorough documentation and Git version control ensure that knowledge is preserved, allowing long-term continuity even if team members change.

By combining these aspects, AutoScholarDB not only supports the Innovation Center’s immediate needs but also provides a sustainable solution that can evolve with institutional requirements.

