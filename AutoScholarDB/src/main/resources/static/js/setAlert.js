// Get the form element
const form = document.querySelector('form');

// Get the input field element
const authorIdInput = document.getElementById('authorid');

// Get the element where the API content will be displayed
// We'll add this element to the HTML in the next step
const contentDisplay = document.getElementById('api-content');

// Add an event listener for the form submission
form.addEventListener('submit', function (event) {
    // 1. Prevent the default form submission (page reload)
    event.preventDefault();

    // 2. Get the author ID entered by the user
    const authorId = authorIdInput.value;

    // Private API key

    // Check if the field is empty
    if (!authorId) {
        contentDisplay.innerHTML = '<p style="color:red;">Please enter an Author ID.</p>';
        return; // Stop execution if the field is empty
    }

    // 3. Construct the API URL
    const apiUrl = `http://localhost:8080/api/scholar?authorId=${authorId}`;

    // 4. Use the fetch API to make the request
    fetch(apiUrl)
        .then(response => {
            // Check if the response was successful (status 200-299)
            if (!response.ok) {
                // Throw an error if the status is not 'ok' (e.g., 404, 500)
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
            // Parse the response body as JSON
            return response.json();
        })
        .then(data => {
            // 5. Handle the retrieved data and display it on the page

            // Assuming your Spring Boot API returns a JSON object 
            // that you want to stringify for simple display.
            const dataString = JSON.stringify(data, null, 2);

            // Display the data in the <p> tag, wrapping it in <pre> for formatted JSON
            contentDisplay.innerHTML = `<p><strong>API Response:</strong></p><pre>${dataString}</pre>`;
        })
        .catch(error => {
            // 6. Handle any errors during the fetch operation (e.g., network issues, API down)
            console.error('Fetch error:', error);
            contentDisplay.innerHTML = `<p style="color:red;">Error retrieving data: ${error.message}. Check the console for details.</p>`;
        });
});