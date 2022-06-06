// Uses async to simplify the code
async function search(query) {
    // Make the web request to the server
    request = await fetch('/search/TopPages?query=' + query);
    
    // Process the response as json
    return await request.json();
};

// Get the form with the id "searchForm"
var searchForm = document.getElementById("searchForm");

async function ShowResults(){
    // Get the value of the input field with id "query"
    var query = document.getElementById("query").value;
    
    // Get the div with the id "results"
    var resultsDiv = document.getElementById("results");
    var resultsTable = document.getElementById("resultsTable");

    resultsDiv.innerHTML = "Loading results from the server...";
    
    results = await search(query);

    resultsDiv.innerHTML = ""; // Done getting results

    console.log(results);
    console.log(resultsDiv);
    // Add the results to the div


    // Clear the table body of all rowss
    var tableBody = document.getElementById("tableBody");
    tableBody.innerHTML = '';

    // Transform the results into table rows
    results.map((result) => {
        var row = document.createElement("tr");
        var column1 = document.createElement("td");
        var column2 = document.createElement("td");
        row.appendChild(column1);
        row.appendChild(column2);

        var link = document.createElement("a");
        link.target="_blank";
        link.rel="noopener noreferrer";
        link.href = result.page.url;
        link.appendChild(document.createTextNode(result.page.title));
        column1.appendChild(link);

        var scoreText = document.createTextNode(result.score);
        column2.appendChild(scoreText);

        return row;
    }).forEach(row => {
        // Append the table rows
        tableBody.appendChild(row);
    });


};

// Get the form element
var form = document.getElementById("searchForm");

// Add a listener for the submit event
form.addEventListener("submit", async function(event){
    // Ignore the default behavior for form submissions
    event.preventDefault();

    // Display the results on this page
    ShowResults();
});

// Run an event when the form is modified
form.addEventListener("input", ShowResults);


// Todo: figure out how to fix pages with multiple urls:
// First article:
// https://en.wikipedia.org/wiki/Po_Chih_Leong
// Slightly different redirect url:
// https://en.wikipedia.org/wiki/Po-Chih_Leong