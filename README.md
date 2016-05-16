# WWI13AMB_Waermepumpen
Repository zum Projekt in 'Neue Konzepte – Software Engineering' an der DHBW-Mannheim von Isabelle Klein und Claudia Gräfe.


##Dokumentation der Wärmepumpen-Steuerung

Das vorliegende Projekt wurde als Leistungsnachweis für die Veranstaltung 'Neue Konzepte – Software Engineering' von den Studierenden Isabelle Klein und Claudia Gräfe des Kurses WWI13AMB der DHBW Mannheim erstellt.
Das Projekt dient der Realisierung einer dynamischen Wärmepumpen-Steuerung für den Raum Heidelberg. 

Der Prototyp demonstriert den An-und Abschalte-Vorgang einer Vielzahl von Wärmepumpen in Abhängigkeit von einem wechselnden Stromverbrauch.
Dabei wurde wurde außer Acht gelassen ob die Testdaten zu 100% realistisch sind. Im Gegensatz dazu wurden für die Testdaten des Stromverbrauchs sogar bewusst sehr hohe Schwankungen berücksichtigt, da für den Prototyp verminderte Zeitintervalle gewählt wurden.
Die Testdaten umfassen:
* 100 Wärmepumpen 
 * in einem festgelegten Bereich der Stadt Heidelberg
 * eine eindeutige ID
 * verfügen über eine Lokation bestehend aus x- und y-Koordinaten
 * einen zufälligen Verbrauchswert zwischen 7 und 14 kWh
 * Offtime, welche bei beginn des Testlaufes auf 0 gesetzt und mit jeder Abschaltung hochgezählt wird

* Stromverbrauchswerte
 * Dauer von 5 Minuten
 * 300 Wertepaare aus einem Zeitwert in Sekunden und einem Verbrauchswert in kWh

* Intervall zur An- und Abschaltung von 5 Sekunden

Für eine spätere Realisierung auf Basis des Prototyps werden realistische Werte für Wärmepumpen und den Stromverbrauch von den Stadtwerken erwartet. Die Intervalle für die An- und Abschaltung würden dann vermutlich auf mindestens 15 Minuten festgelegt.


###Beschreibung des Ausgangsszenarios

Die Stadtwerke Heidelberg als regionaler Stromlieferant haben nur einen begrenzten Strom pro Zeitwert zur Verfügung. Damit sie keinen weiteren Strom teuer von Fremdanbietern beziehen müssen, werden sämtliche Wärmepumpen der Stadt zu Stoßzeiten pauschal abgeschaltet. Längere Abschaltzeiten führen dazu, dass die Wohnhäuser, welche mit den Wärmepumpen beheizt werden, auskühlen. Daher sollten die Wärmepumpen nach Möglichkeit so wenig wie möglich ausgeschaltet sein.
Stoßzeiten sind beispielsweise die Mittagszeit in der die Bewohner der Stadt ihr Mittagessen kochen möchten und daher einen erhöhten Stromverbrauch erzeugen. Das Abschalten aller Pumpen ist jedoch nicht sonderlich effektiv. Daher soll das Programm die An- und Abschaltung der Wärmepumpen dynamisch zum Stromverbrauch steuern.


###Unser Ansatz zur Steuerung der Wärmepumpen
Ziel des Prototyps ist demnach die Optimierung der Offtime jeder einzelnen Wärmepumpe.


###Frameworks

* Apache Spark Streaming zur Verarbeitung von Datenströmen (Apache License 1.6.1)
* Java Spark zur Erstellung der JSON-Datei (Version 2.3)
* Server Socket zum Datenaustausch / Kommunikation (Version 0.7.0)
* JfreeChart zur Darstellung des Stromgraphen (Version 1.0.13)
* Leaflet zur Darstellung der Geo-Map (BSD licenses)
* CanvasJS ein JavaScript Chart zur Visualisierung des aktuellen Stromgraphen der Homepage, welcher die An- und Abschaltung steuert.


##Vorgehensweise 
Die Vorgehensweise wird unterteilt in den automatisierten Ablauf innerhalb des Programms und das Vorgehen eines Users zum Nachvollziehen des Prototyps.

###Vorgehensweise innerhalb des Programms für die Verarbeitung
1.	Erstellung von 100 Wärmepumpen 
2.	Erstellung des individuellen Stromgraphen mit Ausgabe in einem Line Chart
3.	Apache Spark Streaming bildet ein Durchschnitt über ein 5-Sekunden Intervall. 
4.	Diese Daten werden in eine JSON-Datei umgewandelt
5.	Die index.html liest das JSON und gibt die Daten auf der aufgerufenen Homepage aus.

###Vorgehensweise für den User
1.	Programm Starten
2.	Homepage aufrufen (localhost:4567)
Zu beachten ist hierbei, dass Apache Spark Streaming ca. 12. Sekunden benötigt, bis die ersten Werte auf der Homepage angezeigt werden.


###Ergebnisse der Verarbeitung für den Anwender
Karte mit einem umrandeten Ausschnitt von Heidelberg. Dieser Ausschnitt enthält die 100 erstellten Wärmepumpen, die alle 5 Sekunden ihre Marker-Farbe zwischen Rot / Grün, je nach Zustand, wechseln. Rote Marker zeigen eine ausgeschaltete Pumpe und grüne symbolisieren angeschaltete Pumpen.

Auf der rechten Seite wird ein Graph mit dem aktuellen Stromverbrauch angezeigt. Auf Basis dieser Werte erfolgt die Regelung der Wärmepumpen auf der nebenstehenden Karte.


###Allgemeine Ergebnisse
* Ausschaltzeit aller Pumpen wird minimiert. 
* Die Pumpen werden nicht generell abgeschalten. 
* Die Stromauslastung wird auf alle Pumpen gleichmäßig verteilt. 


##Präsentation und Video

Das vorliegende Video enthält eine vollständige Präsentation und Life-Demonstration des Programms:
https://github.com/claudiagraefe/WWI13AMB_Waermepumpen/tree/master/src/main/resources/video

