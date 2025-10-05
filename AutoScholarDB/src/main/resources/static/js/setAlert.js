// Get the form element
const form = document.querySelector('form');

// Get the input field element
const authorIdInput = document.getElementById('authorid');

// Get the element where the API content will be displayed
const contentDisplay = document.getElementById('api-content');

/**
 * Generates the HTML table content from the AuthorInfo JSON object.
 * @param {object} authorInfo - The AuthorInfo DTO returned as JSON.
 * @returns {string} The HTML string for the table.
 */
function createAuthorTableHTML(authorInfo) {
    // Start building the HTML content
    let html = `
        <h1>${authorInfo.name} Articles</h1>
        <p><strong>Affiliations:</strong> ${authorInfo.affiliations || 'N/A'}</p>
        <h2>Articles</h2>
        <table>
            <thead>
                <tr>
                    <th>Title</th>
                    <th>Authors</th>
                    <th>Publication</th>
                    <th>Year</th>
                    <th>Cited By</th>
                </tr>
            </thead>
            <tbody>
    `;

    // Loop through the articles and create table rows
    if (authorInfo.articles && authorInfo.articles.length > 0) {
        authorInfo.articles.forEach(article => {
            // Safely access the nested 'value'
            const citedByValue = article.citedBy ? article.citedBy.value : 0;

            html += `
                <tr>
                    <td>${article.title || 'N/A'}</td>
                    <td>${article.authors || 'N/A'}</td>
                    <td>${article.publication || 'N/A'}</td>
                    <td>${article.year || 'N/A'}</td>
                    <td>${citedByValue}</td>
                </tr>
            `;
        });
    } else {
        html += `<tr><td colspan="5">No articles found for this author.</td></tr>`;
    }

    // Close the table and return the full HTML
    html += `
            </tbody>
        </table>
    `;

    return html;
}

// Add an event listener for the form submission
form.addEventListener('submit', function (event) {
    // 1. Prevent the default form submission (page reload)
    event.preventDefault();

    // 2. Get the author ID entered by the user
    const authorId = authorIdInput.value;

    if (!authorId) {
        contentDisplay.innerHTML = '<p style="color:red;">Please enter an Author ID.</p>';
        return; // Stop execution if the field is empty
    }

    // Set a loading message
    contentDisplay.innerHTML = '<p>Loading scholar information...</p>';

    // 3. Construct the API URL
    const apiUrl = `http://localhost:8080/api/scholar?authorId=${authorId}`;

    // 4. Use the fetch API to make the request
    fetch(apiUrl)
        .then(response => {
            if (!response.ok) {
                // If response is not ok (e.g., 500 error), read the JSON body for the error message
                return response.json().then(err => {
                    throw new Error(err.error || `HTTP error! Status: ${response.status}`);
                });
            }
            // Parse the response body as JSON (which is AuthorInfo DTO)
            return response.json();
        })
        .then(authorInfo => {
            // 5. Handle the retrieved data and display the HTML table
            const htmlContent = createAuthorTableHTML(authorInfo);
            contentDisplay.innerHTML = htmlContent;
        })
        .catch(error => {
            // 6. Handle any errors during the fetch operation
            console.error('Fetch error:', error);
            contentDisplay.innerHTML = `<p style="color:red;">Error retrieving data: ${error.message}. Check the console for details.</p>`;
        });
});