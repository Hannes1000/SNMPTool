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
                    output = "All Working Commands:\n" +
                            Commands.GetSysDesc + " [IP-Address]" + "\n" +
                            Commands.GETSYSOBJECTID + " [IP-Address]" + "\n" +
                            Commands.GETSYSUPTIME + " [IP-Address]" + "\n" +
                            Commands.EXIT + "\n\n" +
                            "Other Commands (Not Working Yet):\n" +
                            Commands.SETSNMP + "\n";
                    break;
                case Commands.GetSysDesc:
                    if(input.length != 2){
                        output = "Please enter also the IP-Address after the Command.\n";
                    }else {
                        output = snmpController.snmpGet(input[1], READ_COMMUNITY, oid);
                    }
                    break;
                case Commands.GETSYSOBJECTID:
                    output = "System Object ID: ";
                    String objid = snmpController.snmpGet(input[1], READ_COMMUNITY, "1.3.6.1.2.1.1.2.0");
                    output += objid;
                    output += "\nSystem: " + snmpController.snmpGet(input[1], READ_COMMUNITY, objid);
                    break;
                case Commands.GETSYSUPTIME:
                    output = snmpController.snmpGet(input[1], READ_COMMUNITY, "1.3.6.1.2.1.1.3.0");
                    break;
                default:
                    output = "Unknown Command:\nType [help] to see all Commands.\n";
                    break;
            }
            System.out.println(output);
        }
    }


    private static class Commands {
        public static final String HELP = "help";
        public static final String EXIT = "exit";
        public static final String GetSysDesc = "sysdesc";
        public static final String GETSYSOBJECTID = "sysobjid";
        public static final String GETSYSUPTIME = "sysuptime";
        public static final String SETSNMP = "setsnmp";
    }

}