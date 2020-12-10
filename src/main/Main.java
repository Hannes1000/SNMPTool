package main;

import jdk.nashorn.internal.objects.annotations.Getter;
import test.Test;

import javax.sound.midi.Soundbank;
import java.util.Scanner;

public class Main {

    public static final String READ_COMMUNITY = "public";

    public static final String WRITE_COMMUNITY= "private";

    public static final int mSNMPVersion = 0; // 0 represents SNMPController version=1

    public static final String OID_UPS_OUTLET_GROUP1 =
            "1.3.6.1.4.1.318.1.1.1.12.3.2.1.3.1";

    public static final String OID_SYS_DESCR="1.3.6.1.2.1.1.1.0";


    public static void main(String[] args) {

        boolean programmrunning = true;
        String strIPAddress = "127.0.0.1";
        SNMPController snmpController = new SNMPController();
        //Set Value=2 to trun OFF UPS OUTLET Group1
        //Value=1 to trun ON UPS OUTLET Group1
        int Value = 2;

        Scanner scanner = new Scanner(System.in);
        String[] input;
        String output = "";

        System.out.println("SNMP Program started:\n" +
                "Enter [help] to get all Commands.\n");
        while (programmrunning){
            input = scanner.nextLine().split(" ");

            switch (input[0]){
                case Commands.EXIT:
                    programmrunning = false;
                    output = "Program shutdown\n";
                    break;
                case Commands.HELP:
                    output = "All Working Commands:\n" +
                            Commands.GETSNMP + " [IP-Address]" + "\n" +
                            Commands.EXIT + "\n\n" +
                            "Other Commands (Not Working Yet):\n" +
                            Commands.SETSNMP + "\n";
                    break;
                case Commands.GETSNMP:
                    if(input.length != 2){
                        output = "Please enter also the IP-Address after the Command.\n";
                    }else {
                        output = snmpController.snmpGet(input[1], READ_COMMUNITY, OID_SYS_DESCR);
                    }
                    break;
                default:
                    output = "Unknown Command:\nType help to see all Commands.\n";
                    break;
            }
            System.out.println(output);
        }
    }


    private static class Commands {
        public static final String HELP = "help";
        public static final String EXIT = "exit";
        public static final String GETSNMP = "getsnmp";
        public static final String SETSNMP = "setsnmp";
    }

}