// api url
const api_url =
    "http://localhost:8080/api/pdus/types";

// Defining async function
async function getapi(url) {

    // Storing response
    const response = await fetch(url);

    // Storing data in form of JSON
    var data = await response.json();
    console.log(data);
    show(data);
}
// Calling that async function
getapi(api_url);
// Function to define innerHTML for HTML table
function show(data) {
    console.log(data.login);
    document.getElementById("pdu-undefined").innerText = data.undefined;
    document.getElementById("pdu-login").innerText = data.login;
    document.getElementById("pdu-logout").innerText = data.logout;
    document.getElementById("pdu-chat").innerText = data.chat;
    document.getElementById("pdu-finished").innerText = data.finish;
}

