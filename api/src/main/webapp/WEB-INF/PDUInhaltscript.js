// api url
const api_url =
    "http://localhost:8080/api/pdus";

// Defining async function
async function getapi(url) {

    // Storing response
    const response = await fetch(url);

    // Storing data in form of JSON
    var data = await response.json();
    console.log(typeof(data));
    if (response) {
        //hideloader();
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
    let tab =
        `<tr>
		<th>clientThread</th>
		<th>auditTime</th>
		<th>serverThread</th>
		<th>id</th>
		<th>pduType</th>
		<th>content</th>
		<th>username</th>

		</tr>`;

    console.log(Array.from(data));
    //data war nicht vorher nicht iterable
    let datenarray = Array.from(data);
    // Loop to access all rows
    for (let r of datenarray) {
        tab += `<tr>
	<td>${r.clientThread} </td>
	<td>${r.auditTime}</td>
	<td>${r.serverThread}</td>
	<td>${r.id}</td>	
	<td>${r.pdutype}</td>	
	<td>${r.content}</td>		
	<td>${r.username}</td>		
	
	
</tr>`;
    }
    // Setting innerHTML as tab variable
    document.getElementById("contentofPDUs").innerHTML = tab;
}
