// api url
const api_url =
    "http://localhost:8080/api/pdus/types";


async function getapi(url) {

    // Speichern der Response
    const response = await fetch(url);

    // Speichern als JSON
    var data = await response.json();
    console.log(data);
    show(data);
}
// Aufruf der asynchronen Funktion
getapi(api_url);

//Befüllung der Tabelle über ID'S (anders als in der anderen javascript)
function show(data) {
    console.log(data.login);
    document.getElementById("pdu-undefined").innerText = data.undefined;
    document.getElementById("pdu-login").innerText = data.login;
    document.getElementById("pdu-logout").innerText = data.logout;
    document.getElementById("pdu-chat").innerText = data.chat;
    document.getElementById("pdu-finished").innerText = data.finish;
}

