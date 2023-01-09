// api url
const api_url =
    "http://localhost:8080/api/pdus";

// Definition einer asynchronen Methode zum api aufruf
async function getapi(url) {

    // Speichern des Response Objects
    const response = await fetch(url); //erstmal ein Promise

    // Daten in JSON Speichern
    var data = await response.json();
    console.log(typeof(data));
    show(data);
}
// Aufruf der asynchronen getapi() Methode
getapi(api_url);


// funktion um inner html zu definieren
function show(data) {
    let tab = "";

    //data iterable machen
    let datenarray = Array.from(data);
    // schleife um Tabelle zu befüllen
    for (let r of datenarray) {
        tab += `<tr class="hover:bg-gray-50">
      <td class="px-6 py-4">${r.id}</td>
      <td class="px-6 py-4 font-normal text-gray-900">
        ${r.clientThread}
      </td>
      <td class="px-6 py-4 font-normal text-gray-900">${r.serverThread}</td>
      <td class="px-6 py-4 font-semibold text-gray-900">
        ${r.auditTime} ms
      </td>
      <td class="px-6 py-4">
            <span
                    class="inline-flex items-center gap-1 rounded-full bg-blue-50 px-2 py-1 text-xs font-semibold text-blue-600"
            >
              ${r.pduType}
            </span>
      </td>
      <td class="px-6 py-4 font-normal text-gray-900">
        ${r.username}
      </td>
      <td class="px-6 py-4 font-normal text-gray-900 overflow-ellipsis">
        ${r.content}
      </td>
    </tr>`;
    }
    // Setting innerHTML as tab variable
    document.getElementById("contentofPDUs").innerHTML = tab;
}
