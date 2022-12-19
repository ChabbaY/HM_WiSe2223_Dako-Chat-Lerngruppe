// api url
const api_url =
    "https://localhost:5001/api/ansprechpartner";

// Defining async function
async function getapi(url) {

    // Storing response
   // console.log(fetch(url , { method: "GET", mode: "no-cors" }).toString());
    let response  = await fetch(url); //mode: "no-cors"
    console.log( response);
    let data =await response.json();
    //console.log(JSON.parse(response));


    // Storing data in form of JSON
    var test1 = '{"Heh": 2, "meh": 3}';
    console.log(test1);
    var test11 =JSON.parse(test1);
    console.log(test11);
    //var data = JSON.stringify(response);
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
    let tab =
        `<tr>
		<th>Logout</th>
		<th>Chat</th>
		<th>Finish</th>
		<th>Login</th>
		<th>Undefined</th>

		</tr>`;

    // Loop to access all rows
    for (let r of data.list) {
        tab += `<tr>
	<td>${r.logout} </td>
	<td>${r.chat}</td>
	<td>${r.finish}</td>
	<td>${r.login}</td>	
	<td>${r.undefined}</td>		
	
</tr>`;
    }
    // Setting innerHTML as tab variable
    document.getElementById("employees").innerHTML = tab;
}

/*fetch("http://localhost:8080/api/pdus/types", { method: "GET", mode: "no-cors" })
    .then(function(response){
        return JSON.parse(response.toString());
      //  return response.json();

    })
    .then(function(pduTypes){
        let placeholder = document.querySelector("#data-output");
        let out = "";
        for(let pduType of pduTypes){
            out += `
			<tr>
				<td>${pduType.logout}'> </td>
				<td>${pduType.chat}'> </td>
				<td>${pduType.finish}</td>
				<td>${pduType.login}</td>
				<td>${pduType.undefined}</td>
			</tr>
		`;
        }

        placeholder.innerHTML = out;
    });

///
*/

