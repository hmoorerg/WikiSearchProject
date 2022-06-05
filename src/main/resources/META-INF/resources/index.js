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

    resultsDiv.innerHTML = "Loading results from the server...";
    
    results = await search(query);

    console.log(results);
    // Add the results to the div
    resultsDiv.innerHTML = results
        .map((result) => `${result.page.title} - Score : ${result.score}`)
        .join("<br>");

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