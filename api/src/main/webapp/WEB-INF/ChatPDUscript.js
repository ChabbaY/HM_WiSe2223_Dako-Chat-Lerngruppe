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
    if (response) {
        hideloader();
    }
    show(data);
}
// Calling that async function
getapi(api_url);

// Function to hide the loader
function hideloader() {
    document.getElementById('loading').style.display = 'none';
}
// Function to define innerHTML for HTML table
function show(data) {
    console.log(data.login);
    let tab =
        `<tr>
          <th>Undefined</th>
          <th>Login</th>
          <th>Chat</th>
          <th>Finish</th>
         </tr>`;

    // Loop to access all rows
    //for (let datas of data) {
        //undefined, login, logout, chat, finish
        tab += `<tr> 
    <td>${data.undefined} </td>
    <td>${data.login} </td>
    <td>${data.logout} </td>
    <td>${data.chat} </td>
    <td>${data.finish} </td>    
</tr>`;
    //}
    // Setting innerHTML as tab variable
    document.getElementById("pdus").innerHTML = tab;
}

