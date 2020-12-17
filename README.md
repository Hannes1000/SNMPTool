# SNMPTool
SNMP Tool für System und Netzwerke zum auslesen von Informationen von verschiedenen Geräten mit Hilfe von SNMP.

# Anfroderunge zum Ausführen des Tools:
- java 8 (empfohlen) oder höher
- Gradle oder SNMP4j library bereits installiert
- Intellij IDEA (empfohlen)

# Ausführen des Programms mit Intellij:
- "File>Open" und das Projekt auswählen, wenn Gradle installiert ist lädt es automatisch die Libraries herunter die 
im File "build.gradle" (SNMP4j) angegeben sind (wenn es nicht automatisch passiert kann man dies mit "gradle build" machen). Anschließend die Main.java ausführen.

# Ausführen des Programms ohne Intellij:
- Benötigt wird ein Text-Editor (z.B.: https://notepad-plus-plus.org/downloads) und eine installierte Java-SDK (https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html)


# Mindestanforderungen
• Eine IP-Adresse mittels SNMP abfragen✔

• von der gescannten Adresse mindestens 6 verschiedene Informationen aus-
lesen

• Von einer bestimmten IP-Adresse mittels OID Informationen auslesen✔

• Änderungen des Community Strings unterstützen und standardmäßig pu-
blic und private als String verwenden✔

• Ein README wie dein Programm kompiliert und ausgeführt werden kann✔


# Zusatz Anforderungen
• Von einer bestimmten IP-Adresse mittels MIB Informationen auslesen

• Traps oder Informs empfangen und ausgeben

• Ein ganzes Netzwerk per SNMP scannen

• Simples User Inferface zum Anzeigen von Informationen (kann auch Kon-
solen basiert sein)
