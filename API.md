# Audit Log API Endpoints #

## Restrictions ##

global max. parameter size 100

## Hello World ##

**localhost:8080/api/**
<p>
    root endpoint
</p>

## PDU ##

**localhost:8080/api/pdus**
<h3>GET</h3>
<ul>
    <li>parameter: id (optional)</li>
    <li>returns: 200</li>
    <li>returns: 400 if id is not a number</li>
    <li>returns: 404 if given id doesn't exist (except 0: get all)</li>
    <li>returns: JSON-list or JSON-object (id, pduType, username, clientThread, serverThread, auditTime, content)</li>
</ul>
<h3>POST</h3>
<ul>
    <li>parameter: pduType, username, clientThread, serverThread, auditTime, content</li>
    <li>returns: 200</li>
    <li>returns: 400 if at least one parameter is too long</li>
    <li>returns: 500 if database error</li>
</ul>
<h3>PUT</h3>
<ul>
    <li>parameter: id, pduType, username, clientThread, serverThread, auditTime, content</li>
    <li>returns: 200</li>
    <li>returns: 400 if id is not a number or at least one parameter is too long</li>
    <li>returns: 404 if given id doesn't exist</li>
    <li>returns: 500 if database error</li>
</ul>
<h3>DELETE</h3>
<ul>
    <li>parameter: id</li>
    <li>returns: 200</li>
    <li>returns: 400 if id is not a number</li>
    <li>returns: 500 if database error</li>
</ul>

## Clients ##

**localhost:8080/api/pdus/clients**
<h3>GET</h3>
<ul>
    <li>parameter: username</li>
    <li>returns: 200</li>
    <li>returns: JSON-object (username, chatMessages, lastLogin, lastLogout, list of pdus)</li>
</ul>

## Types ##

**localhost:8080/api/pdus/types**
<h3>GET</h3>
<ul>
    <li>returns: 200</li>
    <li>returns: JSON-object (undefined, login, logout, chat, finish)</li>
</ul>