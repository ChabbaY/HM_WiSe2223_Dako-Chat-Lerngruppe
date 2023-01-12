# Experimentieranwendung **_Datenkommunikation_**

Dieses Projekt dient als Lehrprojekt für die Entwicklung von verteilten Anwendungen auf Basis von grundlegenden
Programmiertechniken für die Kommunikation wie Sockets. Als Kommunikationsanwendung wird eine einfache Chat-Anwendung in
Java verwendet, bei der mehrere Clients miteinander über eine Chat-Gruppe chatten können. Es handelt sich bei der
Chat-Anwendung um eine Client-/Server-Anwendung.

Die Chat-Anwendung nutzt ein einfaches Chat-Protokoll (Anwendungsprotokoll) zur Kommunikation, das folgende
Nachrichtentypen verwendet:

- Login-Request
- Login-Response
- Login-Event
- Logout-Request
- Logout-Response
- Logout-Event
- ChatMessage-Request
- ChatMessage-Response
- ChatMessage-Events

Die Requests werden vom Chat-Client gesendet, die Events dienen der Verteilung an alle angemeldeten Clients, die
Responses sind die Antworten vom Chat-Server an den initiierenden Chat-Client. Login-Requests dienen der Login-Anfrage
eines Teilnehmers (Clients), Logout-Requests der Logout-Anfrage, ChatMessage-Requests zum Senden einer Chat-Nachricht an
alle angemeldeten Teilnehmer.
Die Verteilung der Events an alle Teilnehmer erfolgt von Chat-Server aus über Event-Nachrichten (Login-Events,
Logout-Event, ChatMessage-Event).

Jeder Chat-Client baut eine TCP-Verbindung zum Chat-Server auf, der an einem dedizierten TCP-Port (Standard: 50001) auf
ankommende Verbindungsaufbauwünsche wartet. Die TCP-Verbindung bleibt bis zum Logout eines Clients bestehen.

## Projekt einrichten

- Gradle 7.5 muss lauffähig sein
- OpenJDK 18 installieren
- Im Projekt SDK 18 einstellen:
    - settings->build->gradle->SDK 18 (bzw. Preferences bei Mac OS)
    - library-settings->SDK 18
- Im Terminal ./gradlew clean aufrufen
- Im Terminal ./gradlew build aufrufen

## Start der Anwendung

Alle Ports, die für die Kommunikation über die verschiedenen Techniken benötigt werden, sind standardmäßig eingestellt.

Standardmäßig werden alle Komponenten auf einem Rechner (localhost) gestartet. Eine Verteilung ist aber möglich.

Für alle Teile sind entsprechende Run-Konfigurationen im Ordner .run verfügbar.

### Server starten

Der Chat-Server wird über den Aufruf von ServerStarter gestartet. Über eine GUI können dann weitere
Parameter eingegeben werden. Alle Parameter können direkt beim Start übergeben werden, die GUI kann über den Parameter
--nogui deaktiviert werden.

### Client starten

Clients werden über den Aufruf von ClientStarter gestartet. In der Client-GUI kann dann die Kommunikationsart durch
Angabe des Servertyps angegeben werden. Alle Parameter können direkt beim Start übergeben werden, die GUI kann über den
Parameter --nogui deaktiviert werden.

### Benchmark-Client starten

Ein Benchmark zur Lasterzeugung und Leistungsmessung wird über den Aufruf von BenchmarkingStarter initiiert. Über
eine GUI können dann die Parameter eingegeben werden (Kommunikationsart durch Angabe des Servertyps,
Chat-Server-Adresse, Anzahl an Clients, Anzahl an Nachrichten pro Client, Nachrichtenlänge, ...).
Über diese GUI kann der Ablauf der Lasterzeugung auch verfolgt werden. Am Ende eines Laufs werden statistische Daten zum
Benchmark ausgegeben, die auch in einer Datei protokolliert werden.
(siehe Datei Benchmarking-ChatApp-Protokolldatei). Alle Parameter können direkt beim Start übergeben werden, die GUI
kann über den Parameter --nogui deaktiviert werden.

### AuditLogServer starten

Aufgabe des AuditLog-Servers ist es, die Chat-Nachrichten in eine Datei/Datenbank zu schreiben.
Der Start des AuditLogServers erfolgt über den Aufruf von ServerStarter im auditlog package. Alle Parameter können
direkt beim Start übergeben werden, die GUI kann über den Parameter --nogui deaktiviert werden.

Vor dem Start des Chat-Servers ist ein AuditLog-Server zu starten, der dann beim Start es Chat-Servers ausgewählt wird.
Der Chat-Server prüft, ob der ausgewählte AuditLog-Server verfügbar ist.
Läuft dieser nicht, arbeitet der Chat-Server ohne Audit-Log.

Der Chat-Server verhält sich als Client gegenüber dem AuditLog-Server und Server für die Chat-Clients.
Der AuditLog-Server ist mit Java TCP Sockets, Java Datagramm Sockets und mit Java RMI implementiert. Im Chat-Server sind
die drei Implementierungsvarianten schon vorbereitet, der Chat-Client kann also über TCP, UDP oder Java RMI mit dem
AuditLog-Server kommunizieren. Die verwendeten PDUs sind über die Java-Klasse AuditLogPdu festgelegt.

### API starten

Vor dem Start des AuditLog-Servers muss die API gestartet werden, falls sie zur Persistierung der AuditLog-PDUs
verwendet werden soll. Dazu ist der gradle-Befehl `gradle appRun` auszuführen, da das gretty-plugin verwendet wird. Die
zugehörige Administrationsanwendung kann über den Aufruf von Admintoolstarter gestartet werden, dabei öffnet sich ein
Tab im Browser unter folgender URL:
http://localhost:63342/dako/dako.api.main/WEB-INF/Startseite.html?_ijt=7d866ptr2kacpje7b6puf41o50&_ij_reload=RELOAD_ON_SAVE

Falls das nicht funktionieren sollte, kann auch auf den Link geklickt werden.

## Projektinhalt

Das Projekt ist als Gradle-Multi-Projekt organisiert (siehe settings.gradle)
die gesamte Anwendung befindet sich im Gradle-Projekt dako.

Im Folgenden werden die einzelnen Ordner kurz erläutert:

### .github

Der Ordner enthält die Konfigurationsdatei für Dependapot, ein Tool von GitHub, das auf Aktualität der dependencies
prüft.

### .run

Run-Konfigurationen für alle Teile des Projekts, um diese in IntelliJ bequem starten zu können.

### api

REST-Schnittstelle mit Datenbank zur Persistierung der AuditLog-PDUs und Auswertung in einer Administrationsanwendung.
Wenn die API gestartet wurde, wird sie bei Start des AuditLogServers, ausgenommen RMI, automatisch als Speicherort
gewählt. Andernfalls wird in eine Textdatei geschrieben.

### auditlogserver

Implementierungen für einen Audit-Log-Server, der von Studierenden in einer Studienarbeit angefertigt wurde.

### benchmark

Simulation von Chat-Clients zur Leistungsmessung und zum Test der Anwendung.

### client

In diesem Ordner befindet sich der Chat-Client mit User Interface als eigenes Gradle-Unterprojekt.

### common

Gemeinsam benutzte Klassen und Interfaces.

### communication

In diesem Ordner befinden sich die Implementierungen der verschiedenen Kommunikationstechniken als eigenes
Gradle-Subprojekt.

### config

In diesem Ordner liegen die CheckStyle-Konfiguration und die Log4j-Konfiguration.

Für das Logging wird durchgängig Log4j2 genutzt. Die Konfiguration der Logs liegt in den Dateien

- log4j2.auditLogServer.xml
- log4j2.benchmarkingClient.xml
- log4j2.chatClient.xml
- log4j2.chatServer.xml

### documentation

In diesem Ordner liegen die Javadoc-Dateien sowie eine Dokumentation des Aufbaus der Benchmarking-Protokolldatei und
eine Dokumentation des Aufbaus der AuditLog-Datei, falls eine Textdatei verwendet wird.

### examples

Einige Programme zum Test von Sockets und Java RMI, die nicht relevant für die Chat-Anwendung sind. Sie dienen nur zum
Ausprobieren der Klassen.

### logs

In diesen Ordner werden die Logdateien abgelegt.

### presentation

Hier liegt die PowerPoint zur Vorstellung des Projekts.

### server

In diesem Ordner befindet sich der Chat-Server mit User Interface als eigenes Gradle-Subprojekt.

### uml

Vorgesehen für UML-Diagramme.

## Sonstiges

### weitere informative Dateien
- API.md beschreibt die verfügbaren Endpunkte der REST-Schnittstelle
- EMOJIS.md beschreibt eine Idee Emojis in der Kommunikation zu kodieren und beim Client dann wieder korrekt
darzustellen (wie das beispielsweise bei Discord der Fall ist). Dies ist jedoch noch nicht implementiert.
- SECURITY.md enthält die Security-Policy. In diesem Fall nicht wirklich relevant aber dennoch enthalten.
- TASKS.md bietet eine Übersicht über Zuständigkeitsbereiche innerhalb des Projekt-Teams.
- Benchmark_TCP_UDP enthält die Benchmarking-Ergebnisse bei Verwendung von TCP und UDP
- Benchmarking_RMI enthält die Benchmarking-Ergebnisse bei Verwendung von RMI

### Unit-Tests

Tests sind teilweise implementiert.

### Advanced Chat

Weiterführende Implementierung des Chat-Protokolls, derzeit nicht vorhanden.