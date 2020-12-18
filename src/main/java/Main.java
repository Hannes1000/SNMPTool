import jdk.nashorn.internal.objects.annotations.Getter;

import javax.sound.midi.Soundbank;
import java.util.Scanner;

public class Main {

    public static final String READ_COMMUNITY = "public";

    public static final String WRITE_COMMUNITY= "private";

    public static final int mSNMPVersion = 0; // 0 represents SNMPController version=1

    public static final String OID_UPS_OUTLET_GROUP1 =
            "1.3.6.1.4.1.318.1.1.1.12.3.2.1.3.1";

    


    public static void main(String[] args) {

        boolean programmrunning = true;
        String oid;
        String defaultOid = "1.3.6.1.2.1.1.1.0";
        oid = defaultOid;
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
                    output = "All Working Commands (default ip: " + strIPAddress +"):\n" +
                            Commands.GetSysDesc + " [IP-Address]" + "\t//Description of the Device\n" +
                            Commands.GETSYSOBJECTID + " [IP-Address]" + "\t//Oject-id that describes the Device\n" +
                            Commands.GETSYSUPTIME + " [IP-Address]" + "\t//Time that the Device is running\n" +
                            Commands.GETSYSCONTACT + " [IP-Address]" + "\t//Contact Person that of the Device\n" +
                            Commands.GETSYSNAME + " [IP-Address]" + "\t//Name of the Device\n" +
                            Commands.GETSYSLOCATION + " [IP-Address]" + "\t\t//Location of the Device (not always accurate because user can set it)\n" +
                            Commands.GETSYSSERVICES + " [IP-Address]" + "\t//How many services are currently running on the Device\n" +
                            Commands.IPADDRESS + " [IP-Address]" + "\t\t//Change the default IP-Address\n" +
                            Commands.EXIT + "\t\t\t//Shut down\n\n" +
                            "Other Commands (Not Working Yet):\n" +
                            Commands.SETSNMP + "\n";
                    break;
                case Commands.IPADDRESS:
                    if(input.length == 2) {
                        output = "Changed " + strIPAddress + " to " + input[1];
                        strIPAddress = input[1];
                    }else {
                        output = "Please enter also an IP-Adress";
                    }
                    break;
                case Commands.GetSysDesc:
                    output = "Device Information: ";
                    if(input.length != 2){
                        output = snmpController.snmpGet(strIPAddress, READ_COMMUNITY, oid);
                    }else {
                        output = snmpController.snmpGet(input[1], READ_COMMUNITY, oid);
                    }
                    break;
                case Commands.GETSYSOBJECTID:
                    if(input.length != 2){
                        output = snmpController.snmpGet(strIPAddress, READ_COMMUNITY, "1.3.6.1.2.1.1.2.0");
                    }else {
                        output = snmpController.snmpGet(input[1], READ_COMMUNITY, "1.3.6.1.2.1.1.2.0");
                    }
                    break;
                case Commands.GETSYSUPTIME:
                    output = "Device is running for: ";
                    if(input.length != 2){
                        output += snmpController.snmpGet(strIPAddress, READ_COMMUNITY, "1.3.6.1.2.1.1.3.0");
                    }else {
                        output += snmpController.snmpGet(input[1], READ_COMMUNITY, "1.3.6.1.2.1.1.3.0");
                    }
                    break;
                case Commands.GETSYSCONTACT:
                    output = "Device Contact: ";
                    if(input.length != 2){
                        output += snmpController.snmpGet(strIPAddress, READ_COMMUNITY, "1.3.6.1.2.1.1.4.0");
                    }else {
                        output += snmpController.snmpGet(input[1], READ_COMMUNITY, "1.3.6.1.2.1.1.4.0");
                    }
                    break;
                case Commands.GETSYSNAME:
                    output = "Device Name: ";
                    if(input.length != 2){
                        output += snmpController.snmpGet(strIPAddress, READ_COMMUNITY, "1.3.6.1.2.1.1.5.0");
                    }else {
                        output += snmpController.snmpGet(input[1], READ_COMMUNITY, "1.3.6.1.2.1.1.5.0");
                    }
                    break;
                case Commands.GETSYSLOCATION:
                    output = "Device Location: ";
                    if(input.length != 2){
                        output += snmpController.snmpGet(strIPAddress, READ_COMMUNITY, "1.3.6.1.2.1.1.6.0");
                    }else {
                        output += snmpController.snmpGet(input[1], READ_COMMUNITY, "1.3.6.1.2.1.1.6.0");
                    }
                    break;
                case Commands.GETSYSSERVICES:
                    output = "Currently Running Services: ";
                    if(input.length != 2){
                        output += snmpController.snmpGet(strIPAddress, READ_COMMUNITY, "1.3.6.1.2.1.1.7.0");
                    }else {
                        output += snmpController.snmpGet(input[1], READ_COMMUNITY, "1.3.6.1.2.1.1.7.0");
                    }
                    break;
                case "test":
                    if(input.length != 2){
                        output = snmpController.snmpGet(strIPAddress, READ_COMMUNITY, "1.3.6.1.2.1.1.4.0");
                    }else {
                        output = snmpController.snmpGet(input[1], READ_COMMUNITY, "1.3.6.1.2.1.1.4.0");
                    };
                default:
                    output = "Unknown Command:\nType [help] to see all Commands.\n";
                    break;
            }
            System.out.println(output);
        }
    }


    public static class Commands {
        public static final String HELP = "help";
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
    }

}