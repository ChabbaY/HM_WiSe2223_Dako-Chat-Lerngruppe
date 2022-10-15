# Experimentieranwendung **_Datenkommunikation_**
Dieses Projekt dient als Lehrprojekt für die Entwicklung von verteilten Anwendungen auf Basis von grundlegenden Programmiertechniken für die Kommunikation wie Sockets. Als Kommunikationsanwendung wird eine einfache Chat-Anwendung in Java verwendet, bei der mehrere Clients miteinander über eine Chat-Gruppe chatten können. Es handelt sich bei der Chat-Anwendung um eine Client-/Server-Anwendung.

Die Chat-Anwendung nutzt ein einfaches Chat-Protokoll (Anwendungsprotokoll) zur Kommunikation, das folgende Nachrichtentypen verwendet:

- Login-Request
- Login-Response
- Login-Event
- Logout-Request
- Logout-Response
- Logout-Event
- ChatMessage-Request
- ChatMessage-Response
- ChatMessage-Events

Die Requests werden vom Chat-Client gesendet, die Events dienen der Verteilung an alle angemeldeten Clients, die Responses sind die Antworten vom Chat-Server an den initiierenden Chat-Client. Login-Requests dienen der Login-Anfrage eines Teilnehmers (Clients), Logout-Requests der Logout-Anfrage, ChatMessage-Requests zum Senden einer Chat-Nachricht an alle angemeldeten Teilnehmer.
Die Verteilung der Events an alle Teilnehmer erfolgt von Chat-Server aus über Event-Nachrichten (Login-Events, Logout-Event, ChatMessage-Event).

Jeder Chat-Client baut eine TCP-Verbindung zum Chat-Server auf, der an einem dedizierten TCP-Port (Standard: 50001) auf ankommende Verbindungsaufbauwünsche wartet. Die TCP-Verbindung bleibt bis zum Logout eines Clients bestehen.

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

### Server starten
Der Chat-Server wird Clients werden über den Aufruf von ChatServerStarter gestartet. Über eine GUI können dann weitere Parameter eingegeben werden.

### Client starten
Clients werden über den Aufruf von ChatClientStarter gestartet. In der Client-GUI kann dann die Kommunikationsart durch Angabe des Servertyps angegeben werden.

### Benchmark-Client starten
Ein Benchmark zur Lasterzeugung und Leistungsmessung wird über den Aufruf von BenchmarkingClientStarter initiiert. Über eine GUI können dann die Parameter eingegeben werden (Kommunikationsart durch Angabe des Servertyps, Chat-Server-Adresse, Anzahl an Clients, Anzahl an Nachrichten pro Client, Nachrichtenlänge, ...).
Über diese GUI kann der Ablauf der Lasterzeugung auch verfolgt werden. Am Ende eines Laufs werden statistische Daten zum Benchmark ausgegeben, die auch in einer Datei protokolliert werden.
(siehe Datei Benchmarking-ChatApp-Protokolldatei).

### AuditLogServer starten
Aufgabe des AuditLog-Servers ist es, die Chat-Nachrichten in eine Datei zu schreiben.
Der Start des für die Studierenden nicht sichtbaren Servers erfolgt über die Kommandozeile oder in der IDE.

Vor dem Start des Chat-Servers ist ein AuditLog-Server zu starten, der dann beim Start es Chat-Servers ausgewählt wird. Der Chat-Server prüft, ob der ausgewählte AuditLog-Server verfügbar ist.
Läuft dieser nicht, arbeitet der Chat-Server ohne Audit-Log.

Der Chat-Server verhält sich als Client gegenüber dem AuditLog-Server und Server für die Chat-Clients.
Der AuditLog-Server ist mit Java TCP Sockets, Java Datagramm Sockets und mit Java RMI implementiert. Im Chat-Server sind die drei Implementierungsvarianten schon vorbereitet, der Chat-Client kann also über TCP, UDP oder Java RMI mit dem AuditLog-Server kommunizieren. Die verwendeten PDUs sind über die Java-Klasse AuditLogPdu festgelegt.


## Projektinhalt
Das Projekt ist als Gradle-Multi-Projekt organisiert (siehe settings.gradle)
die gesamte Anwendung befindet sich im Gradle-Projekt dako. 

Im Folgenden werden die einzelnen Ordner kurz erläutert:

### examples
Einige Programme zum Test von Sockets und Java RMI, die nicht relevant für die Chat-Anwendung sind. Sie dienen nur zum Ausprobieren der Klassen.

### benchmark
Simulation von Chat-Clients zur Leistungsmessung und zum Test der Anwendung. 

### client
In diesem Ordner befindet sich der Chat-Client mit User Interface als eigenes Gradle-Unterprojekt.

### common
Gemeinsam benutzte Klassen und Interfaces.

### communication
In diesem Ordner befinden sich die Implementierungen der verschiedenen Kommunikationstechniken als eigenes Gradle-Unterprojekt.

### server
In diesem Ordner befindet sich der Chat-Server mit User Interface als eigenes Gradle-Unterprojekt.

### audit log server
Einfache Beispiel-Implementierungen für einen Audit-Log-Server, den die Studierenden in einer Studienarbeit anfertigen sollen.

### config
In diesem Ordner liegen die CheckStyle-Konfiguration und die Log4j-Konfiguration.

Für das Logging wird durchgängig Log4j2 genutzt. Die Konfiguration der Logs liegt in den Dateien
- log4j2.benchmarkingClient.xml
- log4j2.chatClient.xml
- log4j2.chatServer.xml
- log4j2.auditLogRmiServer.xml 
- log4j2.auditLogTcpServer.xml
- log4j2.auditLogUdpServer.xml

### documentation
In diesem Ordner liegen die Javadoc-Dateien sowie eine Dokumentation des Aufbaus der Benchmarking-Protokolldatei.

### logs
In diesen Ordner werden die Logdateien abgelegt.

### uml
Vorgesehen für UML-Diagramme.


## Sonstiges

### Unit-Tests
Tests sind noch nicht implementiert.


### Advanced Chat
Weiterführende Implementierung des Chat-Protokolls, derzeit nicht vorhanden.