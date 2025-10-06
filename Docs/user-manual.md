# 🧭 User Manual — AutoScholarDB Web Interface

This user manual provides detailed guidance on how to use the **AutoScholarDB** web interface to search for authors, view their articles, and manage your local database.

---

## 🔗 Accessing the Application

To start using the application, open your browser and go to:

```
http://localhost:8080/views/index.html
```

This will open the main interface, where you can perform searches, visualize author data, and manage article records.

---

## 🔍 1) Search for an Author by ID

The main interface displays a **search bar** where users can enter the **Author ID** to retrieve information.

When the user enters an author’s ID and submits the search, the system performs a **request to the SerpApi API**, which collects detailed data about that author and their publications.

**Example:**  
If the user enters `E3abc45` as the Author ID, the app will automatically fetch the author’s details and their related articles from the API.

**Illustration:**  
<img width="1871" height="1162" alt="image" src="https://github.com/user-attachments/assets/5c5a92c1-21c1-4304-b5fb-f437efe413bd" />


---

## 🧾 2) Viewing Author and Article Data

After the API request is completed, the system displays a table with the author’s data and related articles.

The following information will appear on the screen:

- **Author Name:** The full name of the author.  
- **Affiliations:** The institutions or organizations the author is affiliated with.  
- **Articles Table:** A structured list showing detailed information about each publication.

**Table Columns:**
| Action | Title | Authors | Publication | Link | Year | Cited By |
|:--|:--|:--|:--|:--|:--|:--|
| Save in Database | Title of the article | List of authors | Journal or conference | Direct link to the article | Year of publication | Number of citations |

**Illustration:**  
<img width="1872" height="1092" alt="image" src="https://github.com/user-attachments/assets/c2349407-3124-4b1e-bb1d-46358b3b567c" />

---

## 💾 3) Saving Articles to the Local Database

Each article row includes an **Add** button (or “Save in Database”).  
By clicking this button, the selected article will be saved into the **local PostgreSQL database**.

Behind the scenes, this triggers a **POST request** to the endpoint:

```
POST http://localhost:8080/db/save
```

The system validates the data and stores it safely in the local database.

**Illustration:**  
<img width="1874" height="1088" alt="image" src="https://github.com/user-attachments/assets/0c75214b-d6f5-4a95-a00f-dc6429297867" />


---

## 📚 4) Viewing Saved Database Content

Users can review the articles stored in the database by clicking the **“View Database Content”** button.

This action displays a new section or page showing all articles currently stored in the local PostgreSQL database.  
Each record includes details such as the title, publication, authors, and citation metrics.

This feature helps users confirm that their data has been successfully saved and provides an overview of all stored research.

**Illustration:**  
<img width="1871" height="1091" alt="image" src="https://github.com/user-attachments/assets/1d962a95-f475-4983-bc95-376e9bbb17a1" />


---

## ✅ Summary of Main Features

| Feature | Description |
|----------|--------------|
| Search by Author ID | Fetch author and article data using SerpApi |
| Display Results | View detailed author and publication information |
| Save Articles | Add selected articles to your local database |
| View Database Content | Review all saved records directly from the interface |

---

## 🛠️ Troubleshooting

- **No results found:**  
  Ensure the Author ID is valid and that the SerpApi key is correctly configured in the backend.
  
- **Error when saving an article:**  
  Check that the PostgreSQL service is running and properly connected.

- **Slow response:**  
  SerpApi requests may take several seconds depending on API limits or connection speed.

---

## 🧑‍💻 Technical Notes

- Backend: **Spring Boot (Java)**  
- Database: **PostgreSQL**  
- External API: **SerpApi**  
- Frontend: **HTML + JavaScript interface** served from `/views/index.html`
