const form = document.querySelector('form');
const authorIdInput = document.getElementById('authorid');
const contentDisplay = document.getElementById('api-content');
const viewDbButton = document.getElementById('viewDbButton');

// Global store for articles retrieved from the last API search
let currentArticles = [];

// ----------------------------------------------------------------------
// Function to handle saving an article to the database
// ----------------------------------------------------------------------

/**
 * Sends a POST request to the Spring Boot endpoint to save an article.
 * @param {object} articleData The Article DTO object to save.
 * @param {HTMLElement} button The button element to update status on.
 */
async function saveArticleToDb(articleData, button) {
    button.disabled = true;
    button.textContent = 'Saving...';
    button.classList.add('saving');

    const dbSaveUrl = 'http://localhost:8080/db/save';

    try {
        const response = await fetch(dbSaveUrl, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(articleData),
        });

        if (!response.ok) {
            const errorData = await response.json();
            throw new Error(errorData.error || `Failed to save article. Status: ${response.status}`);
        }

        const savedEntity = await response.json();

        button.textContent = 'Saved!';
        button.classList.remove('saving');
        button.classList.add('saved');
        console.log('Article saved successfully. ID:', savedEntity.id);

    } catch (error) {
        console.error('Save Article Error:', error);
        button.textContent = 'Failed';
        button.classList.remove('saving');
        button.classList.add('failed');
        // Re-enable button on failure for potential retry
        button.disabled = false;

        // Reset the button text after a delay if it failed
        setTimeout(() => {
            button.textContent = 'Add';
            button.classList.remove('failed');
        }, 3000);
    }
}

// ----------------------------------------------------------------------
// Function to generate SerpAPI table (updated to attach event data)
// ----------------------------------------------------------------------

/**
 * Generates the HTML table content from the AuthorInfo JSON object (SerpAPI result).
 * @param {object} authorInfo - The AuthorInfo DTO returned as JSON.
 * @returns {string} The HTML string for the table.
 */
function createAuthorTableHTML(authorInfo) {
    // Store the articles globally for later use by the "Add" buttons
    currentArticles = authorInfo.articles || [];

    let html = `
        <h1>${authorInfo.name} Articles (from SerpAPI)</h1>
        <p><strong>Affiliations:</strong> ${authorInfo.affiliations || 'N/A'}</p>
        <h2>Articles</h2>
        <div class="table-responsive">
            <table>
                <thead>
                    <tr>
                        <th>Save in Database</th>
                        <th>Title</th>
                        <th>Authors</th>
                        <th>Publication</th>
                        <th>Link</th>
                        <th>Year</th>
                        <th>Cited By</th>
                    </tr>
                </thead>
                <tbody>
    `;

    if (currentArticles.length > 0) {
        currentArticles.forEach((article, index) => {
            const citedByValue = article.citedBy ? article.citedBy.value : 0;

            html += `
                <tr>
                    <td>
                        <!-- data-index is crucial: it tells the JS which article from currentArticles array to save -->
                        <button type="button" class="save-button" data-index="${index}">Add</button>
                    </td>
                    <td>${article.title || 'N/A'}</td>
                    <td>${article.authors || 'N/A'}</td>
                    <td>${article.publication || 'N/A'}</td>
                    <td><a href="${article.link}" target="_blank">${article.link || 'N/A'}</a></td>
                    <td>${article.year || 'N/A'}</td>
                    <td>${citedByValue}</td>
                </tr>
            `;
        });
    } else {
        html += `<tr><td colspan="7">No articles found for this author.</td></tr>`;
    }

    html += `
                </tbody>
            </table>
        </div>
    `;

    return html;
}

// ----------------------------------------------------------------------
// Function to format articles retrieved from the database
// ----------------------------------------------------------------------

/**
 * Generates the HTML table content from a list of ArticleEntity objects (from DB).
 * @param {Array<object>} articles - The list of ArticleEntity objects.
 * @returns {string} The HTML string for the table.
 */
function createDatabaseTableHTML(articles) {
    let html = `
        <h1>Saved Database Articles</h1>
        <p>Showing ${articles.length} articles currently saved in the database.</p>
        <div class="table-responsive">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Authors</th>
                        <!-- RENAMED for clarity: this column contains PUBLICATION NAME + YEAR -->
                        <th>Publication Info (Date)</th> 
                        <th>Cited By</th>
                        <th>Link</th>
                    </tr>
                </thead>
                <tbody>
    `;

    if (articles && articles.length > 0) {
        articles.forEach(article => {
            // We only show article.publicationDate, which is the combined string from the service
            html += `
                <tr>
                    <td>${article.id}</td>
                    <td>${article.title || 'N/A'}</td>
                    <td>${article.authors || 'N/A'}</td>
                    <td>${article.publicationDate || 'N/A'}</td> 
                    <td>${article.citedBy || 0}</td>
                    <td><a href="${article.link}" target="_blank">${article.link || 'N/A'}</a></td>
                </tr>
            `;
        });
    } else {
        html += `<tr><td colspan="6">The database currently contains no saved articles.</td></tr>`;
    }

    html += `
                </tbody>
            </table>
        </div>
    `;
    return html;
}

// ----------------------------------------------------------------------
// Event Listener for Form Submission (Search)
// ----------------------------------------------------------------------

form.addEventListener('submit', function (event) {
    event.preventDefault();
    const authorId = authorIdInput.value;

    if (!authorId) {
        contentDisplay.innerHTML = '<p class="error-message">Please enter an Author ID.</p>';
        return;
    }

    contentDisplay.innerHTML = '<p class="loading-message">Loading scholar information...</p>';
    const apiUrl = `http://localhost:8080/api/scholar?authorId=${authorId}`;

    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => {
                    throw new Error(err.error || `HTTP error! Status: ${response.status}`);
                });
            }
            return response.json();
        })
        .then(authorInfo => {
            const htmlContent = createAuthorTableHTML(authorInfo);
            contentDisplay.innerHTML = htmlContent;
        })
        .catch(error => {
            console.error('Fetch error:', error);
            contentDisplay.innerHTML = `<p class="error-message">Error retrieving data: ${error.message}. Check the console for details.</p>`;
        });
});

// ----------------------------------------------------------------------
// Event Listener for Database Button Click (View DB)
// ----------------------------------------------------------------------

viewDbButton.addEventListener('click', function () {
    authorIdInput.value = '';
    contentDisplay.innerHTML = '<p class="loading-message">Loading database content from Spring Boot...</p>';
    const dbApiUrl = 'http://localhost:8080/db/articles';

    fetch(dbApiUrl)
        .then(response => {
            if (!response.ok) {
                throw new Error(`Failed to fetch database content. Status: ${response.status}`);
            }
            return response.json();
        })
        .then(articlesList => {
            const htmlContent = createDatabaseTableHTML(articlesList);
            contentDisplay.innerHTML = htmlContent;
        })
        .catch(error => {
            console.error('Database Fetch Error:', error);
            contentDisplay.innerHTML = `<p class="error-message">Database access error: ${error.message}. Ensure your Spring Boot service is running and the database is accessible.</p>`;
        });
});


// ----------------------------------------------------------------------
// Event Listener for dynamically created "Add" buttons using delegation
// ----------------------------------------------------------------------

contentDisplay.addEventListener('click', function (event) {
    // Check if the clicked element is an "Add" button and has the necessary data
    if (event.target.classList.contains('save-button')) {
        const button = event.target;
        // Get the index of the article from the button's data attribute
        const index = button.getAttribute('data-index');

        if (index !== null && currentArticles[index]) {
            const articleToSave = currentArticles[index];
            // Call the save function with the article data and the button element
            saveArticleToDb(articleToSave, button);
        } else {
            console.error('Error: Could not retrieve article data for index:', index);
        }
    }
});
