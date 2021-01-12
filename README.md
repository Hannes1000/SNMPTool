# SNMPTool
SNMP Tool für System und Netzwerke zum auslesen von Informationen von verschiedenen Geräten mit Hilfe von SNMP.

# Anfroderunge zum Ausführen des Tools:
- java 8 (empfohlen) oder höher

# Ausführen des Programmes:
- am einfachsten ist es das Projekt in IntellijIDEA zu Importieren und Auszuführen
- ansonsten muss es über die CMD gestarted werden

# Mindestanforderungen
• Eine IP-Adresse mittels SNMP abfragen✔

• von der gescannten Adresse mindestens 6 verschiedene Informationen aus-
lesen✔

• Von einer bestimmten IP-Adresse mittels OID Informationen auslesen✔

• Änderungen des Community Strings unterstützen und standardmäßig pu-
blic und private als String verwenden✔

• Ein README wie dein Programm kompiliert und ausgeführt werden kann✔


# Zusatz Anforderungen
• Von einer bestimmten IP-Adresse mittels MIB Informationen auslesen✔

• Traps oder Informs empfangen und ausgeben

• Ein ganzes Netzwerk per SNMP scannen✔

• Simples User Inferface zum Anzeigen von Informationen (kann auch Kon-
solen basiert sein)✔

# Working Features
• SNMP get: get Information from a Device with the IP-Address, the OID or with a MIB-File

• SNMP set: set the Information of a Device with the IP-Address, the OID or with a MIB-File

• SNMP Discover: searchs the whole Network for Devices which support SNMP

• SNMP MIB: output/read MIB-Files

# Future Steps
• Creating a Jar with that supports MIB

• Creating a Installer to simplify the Installation
