# 📝 AutoScholarDB — Full API Documentation

This document includes complete API documentation for all controllers in the `com.autoscholardb.demo.controller` package:
- `AuthorInfoController` — `GET /api/scholar` (External Google Scholar fetch + normalization)
- `ArticleSaveController` — `POST /db/save` (Save single Article DTO to PostgreSQL)
- `ArticleDatabaseController` — `GET /db/articles` (Fetch all saved articles from DB)

---

## 📚 Table of Contents
- [Overview](#-overview)
- [AuthorInfoController — GET /api/scholar](#authorinfocontroller---get-apischolar)
  - [Description](#description)
  - [Endpoint Details](#endpoint-details)
  - [Request Example](#request-example)
  - [Successful Response (200 OK)](#successful-response-200-ok)
  - [Error Responses](#error-responses)
  - [Implementation Notes](#implementation-notes)
- [ArticleSaveController — POST /db/save](#articlesavecontroller---post-dbsave)
  - [Description](#description-1)
  - [Endpoint Details](#endpoint-details-1)
  - [Request Body (Article DTO)](#request-body-article-dto)
  - [Successful Response (201 Created)](#successful-response-201-created)
  - [Error Responses](#error-responses-1)
  - [Implementation Notes](#implementation-notes-1)
- [ArticleDatabaseController — GET /db/articles](#articledatabasecontroller---get-dbarticles)
  - [Description](#description-2)
  - [Endpoint Details](#endpoint-details-2)
  - [Successful Response (200 OK)](#successful-response-200-ok-1)
  - [Error Responses](#error-responses-2)
  - [Implementation Notes](#implementation-notes-2)
- [Models (DTOs & Entities)](#models-dtos--entities)
- [Service Layer (expected methods)](#service-layer-expected-methods)
- [Error handling & Logging recommendations](#error-handling--logging-recommendations)
- [Curl examples](#curl-examples)
- [File locations](#file-locations)
- [Suggested improvements / next steps](#suggested-improvements--next-steps)

---

## 🧾 Overview
These controllers form the API surface of AutoScholarDB. The controllers are designed for a small internal application and currently do not enforce external authentication in these endpoints — authentication should be added for production.

---

## AuthorInfoController — GET /api/scholar

### Description
Fetches raw data from the Google Scholar API through `ScholarService`, normalizes the JSON response (flattens nested `author` fields, converts snake_case keys to camelCase where required), and returns an `AuthorInfo` DTO to clients.

### Endpoint Details
- **Method:** `GET`  
- **URL:** `/api/scholar`  
- **Query Parameters:**  
  - `authorId` (string) — Google Scholar Author ID (e.g., `qc6CJjYAAAAJ`) — **required**

### Request Example
```
GET /api/scholar?authorId=qc6CJjYAAAAJ HTTP/1.1
Host: localhost:8080
Accept: application/json
```

### Successful Response (200 OK)
```json
{
  "name": "Dr. John Doe",
  "affiliations": "University of Northern Mexico",
  "email": "john.doe@unm.edu",
  "interests": ["Machine Learning", "Educational Data Mining"],
  "articles": [
    {
      "title": "AI in Education",
      "year": "2023",
      "link": "https://scholar.google.com/abc",
      "citedBy": {
        "value": 42
      }
    }
  ]
}
```
**Notes:** `citedBy` may be represented either as an object (`{ "value": 42 }`) depending on the external API or as an integer — the controller currently preserves the nested object shape where present and maps to DTOs using GSON.

### Error Responses
- `400 Bad Request` — Missing `authorId` parameter (controller currently returns `500` on unexpected issues; consider returning `400` if `authorId` absent).  
  Example:
  ```json
  { "error": "Missing query parameter: authorId" }
  ```
- `500 Internal Server Error` — Error contacting external API, JSON transform error, or other unexpected exception.  
  Example:
  ```json
  { "error": "Error fetching author data: <message>" }
  ```

### Implementation Notes
- The controller uses `scholarService.fetchAuthorArticlesApi(authorId).join()` which returns a `JsonObject`. The controller flattens nested `author` fields and renames `cited_by` → `citedBy`. Keep an eye on NullPointerExceptions if expected JSON nodes are missing. Add defensive checks.
- Consider using DTO adapters or custom GSON TypeAdapters to centralize transformation logic and keep controller code thin.
- Consider returning `ResponseEntity.badRequest()` for missing/invalid input rather than a `500` error.

---

## ArticleSaveController — POST /db/save

### Description
Accepts an `Article` DTO in JSON form, performs lightweight validation, transforms it into an `ArticleEntity`, and persists it in PostgreSQL using `ArticleDatabaseService`.

### Endpoint Details
- **Method:** `POST`  
- **URL:** `/db/save`  
- **Content-Type:** `application/json`

### Request Body (Article DTO)
Fields expected in the DTO (JSON):
```json
{
  "title": "Machine Learning in Higher Education",
  "authors": "Jane Doe, John Doe",
  "publication": "Journal of EdTech",
  "year": "2024",
  "link": "https://scholar.google.com/article/xyz",
  "abstractText": "Short summary...",
  "keywords": "education, machine learning",
  "citedBy": { "value": 10 }
}
```
**Validation rules (current implementation):**
- `title` must be present and non-empty. (Responds with `400 Bad Request` if empty)
- Additional validation (e.g., URL format, year numeric) can be added later via `@Valid` + bean validation annotations.

### Successful Response (201 Created)
Returns persisted `ArticleEntity` (example):
```json
{
  "id": 15,
  "title": "Machine Learning in Higher Education",
  "authors": "Jane Doe, John Doe",
  "publicationDate": "Journal of EdTech, 2024",
  "abstractText": "Short summary...",
  "link": "https://scholar.google.com/article/xyz",
  "keywords": "education, machine learning",
  "citedBy": 10
}
```
**Notes:** The controller returns the saved entity. The `ArticleDatabaseService` should map nested `citedBy.value` to the `citedBy` integer column in the DB entity.

### Error Responses
- `400 Bad Request` — Missing/invalid payload (e.g., empty title).  
  ```json
  { "error": "Article title cannot be empty." }
  ```
- `500 Internal Server Error` — DB constraint violation or persistence error.
  ```json
  { "error": "Could not save article: <message>" }
  ```

### Implementation Notes
- Use `@Valid` on the DTO and annotate DTO fields (`@NotBlank`, `@Size`, `@Pattern`) to move validation logic out of the controller and into the framework's validation pipeline.
- Convert `Article` DTO → `ArticleEntity` inside the `ArticleDatabaseService` to keep controller minimal.
- Ensure transactional boundaries in the service layer with `@Transactional` when performing saves or batch operations.

---

## ArticleDatabaseController — GET /db/articles

### Description
Returns all `ArticleEntity` records stored in the PostgreSQL `scholarly_articles` table.

### Endpoint Details
- **Method:** `GET`  
- **URL:** `/db/articles`

### Successful Response (200 OK)
Returns a JSON array of saved entities:
```json
[
  {
    "id": 1,
    "title": "AI and Learning Analytics",
    "authors": "Alice Smith",
    "publicationDate": "Learning Journal, 2023",
    "abstractText": "Summary...",
    "link": "https://scholar.google.com/article/abc",
    "keywords": "ai, analytics",
    "citedBy": 25
  },
  {
    "id": 2,
    "title": "Data-Driven Education",
    "authors": "Bob Lee",
    "publicationDate": "Education Review, 2024",
    "abstractText": "Summary...",
    "link": "https://scholar.google.com/article/xyz",
    "keywords": "data, education",
    "citedBy": 40
  }
]
```

### Error Responses
- `500 Internal Server Error` — Database access error or unexpected exception.
```json
{ "error": "Database fetch error: <message>" }
```

### Implementation Notes
- The controller catches generic `Exception`. Consider handling specific exceptions (e.g., `DataAccessException`) and returning clearer messages.
- For large data sets, consider pagination (`Pageable`) and filtering parameters (e.g., `?author=...`, `?year=...`).

---

## Models (DTOs & Entities)

### Article DTO (expected)
```java
public class Article {
    private String title;
    private String authors; // comma-separated
    private String publication; // name
    private String year; // string or int
    private String link;
    private String abstractText;
    private String keywords; // comma-separated
    private CitedBy citedBy; // { "value": int }
    // getters/setters...
}
```

### ArticleEntity (expected)
```java
@Entity
@Table(name = "scholarly_articles")
public class ArticleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String authors;
    @Column(name = "publication_date")
    private String publicationDate;
    @Column(columnDefinition = "TEXT")
    private String abstractText;
    private String link;
    private String keywords;
    @Column(name = "cited_by")
    private Integer citedBy;
    // getters/setters...
}
```

### AuthorInfo DTO (expected)
```java
public class AuthorInfo {
    private String name;
    private String affiliations;
    private String email;
    private List<String> interests;
    private List<Article> articles;
    // getters/setters...
}
```

---

## Service Layer (expected methods)

### `ArticleDatabaseService`
- `ArticleEntity saveArticle(Article articleDto)` — transforms DTO to entity and persists.
- `List<ArticleEntity> findAllArticles()` — returns all saved articles.

### `ScholarService`
- `CompletableFuture<JsonObject> fetchAuthorArticlesApi(String authorId)` — contacts Google Scholar / SerpAPI and returns parsed JSON.

---

## Error handling & Logging recommendations
- Replace `System.err.println(...)` with a logger (SLF4J/Logback): `private static final Logger logger = LoggerFactory.getLogger(ClassName.class);`
- Return appropriate HTTP codes:
  - `400` for client errors / validation failures
  - `401` / `403` for auth/permission issues (when added)
  - `404` when a requested resource is missing
  - `500` for unexpected server errors
- Provide consistent error response structure:
```json
{
  "timestamp": "2025-10-05T12:00:00Z",
  "status": 500,
  "error": "Internal Server Error",
  "message": "Detailed message",
  "path": "/db/save"
}
```

---

## Curl examples

### Fetch author info
```bash
curl -s "http://localhost:8080/api/scholar?authorId=qc6CJjYAAAAJ" | jq
```

### Save article
```bash
curl -X POST http://localhost:8080/db/save   -H "Content-Type: application/json"   -d '{
    "title":"Machine Learning in Higher Education",
    "authors":"Jane Doe, John Doe",
    "publication":"Journal of EdTech",
    "year":"2024",
    "link":"https://scholar.google.com/article/xyz",
    "abstractText":"Short summary",
    "keywords":"education,ml",
    "citedBy": {"value": 10}
  }' | jq
```

### Get all saved articles
```bash
curl -s "http://localhost:8080/db/articles" | jq
```

---

## File locations
Controller Java files:
- `src/main/java/com/autoscholardb/demo/controller/AuthorInfoController.java`
- `src/main/java/com/autoscholardb/demo/controller/ArticleSaveController.java`
- `src/main/java/com.autoscholardb/demo/controller/ArticleDatabaseController.java`

Service layer (expected):
- `src/main/java/com/autoscholardb/demo/services/ScholarService.java`
- `src/main/java/com.autoscholardb/demo/services/ArticleDatabaseService.java`

Model classes:
- `src/main/java/com/autoscholardb/demo/model/Author/AuthorInfo.java`
- `src/main/java/com.autoscholardb/demo/model/Articles/Article.java`
- `src/main/java/com/autoscholardb/demo/model/Articles/ArticleEntity.java`

---

## Suggested improvements / next steps
1. Add input validation using `@Valid` + Bean Validation API.  
2. Introduce DTO mappers (MapStruct or manual mappers) to avoid duplication.  
3. Replace `System.err` prints with SLF4J logging.  
4. Implement pagination and filters for `GET /db/articles`.  
5. Add authentication/authorization (API key, OAuth, JWT) for production.  
6. Add unit and integration tests for controllers and service layer.

---

