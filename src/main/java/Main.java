package main.java;

import jdk.nashorn.internal.objects.annotations.Getter;

import javax.sound.midi.Soundbank;
import java.awt.*;
import java.io.Console;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static boolean ansiEscapeSupported = false;

    public static void main(String[] args) throws IOException {
        Controller c = new Controller();
        c.startProgrammLoop();
    }

    public static class Commands {




        /*public static final String HELP = "help";
        public static final String EXIT = "exit";
        public static final String IPADDRESS = "setip";
        public static final String GetSysDesc = "sysdesc";
        public static final String GETSYSOBJECTID = "sysobjid";
        public static final String GETSYSUPTIME = "sysuptime";
        public static final String GETSYSCONTACT = "syscont";
        public static final String GETSYSNAME = "sysname";
        public static final String GETSYSLOCATION = "sysloc";
        public static final String GETSYSSERVICES = "sysserv";
        public static final String SETSNMP = "setsnmp";

        public String toString() {
            String ret = "";
            ret +=  Commands.GetSysDesc + " [IP-Address]" + "\t//Description of the Device\n" +
                    Commands.GETSYSOBJECTID + " [IP-Address]" + "\t//Oject-id that describes the Device\n" +
                    Commands.GETSYSUPTIME + " [IP-Address]" + "\t//Time that the Device is running\n" +
                    Commands.GETSYSCONTACT + " [IP-Address]" + "\t//Contact Person that of the Device\n" +
                    Commands.GETSYSNAME + " [IP-Address]" + "\t//Name of the Device\n" +
                    Commands.GETSYSLOCATION + " [IP-Address]" + "\t\t//Location of the Device (not always accurate because user can set it)\n" +
                    Commands.GETSYSSERVICES + " [IP-Address]" + "\t//How many services are currently running on the Device\n" +
                    Commands.IPADDRESS + " [IP-Address]" + "\t\t//Change the default IP-Address\n" +
                    Commands.EXIT + "\t\t\t\t\t//Shut down\n\n" +
                    "Other Commands (Not Working Yet):\n" +
                    Commands.SETSNMP + "\n";
            return ret;
        }*/
    }
/*
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
*/
}