package main.java;

import net.percederberg.mibble.MibLoaderException;
import net.percederberg.mibble.MibSymbol;
import net.percederberg.mibble.MibValueSymbol;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import sun.nio.cs.KOI8_U;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Scanner;

public class Controller {

    //*SNMP Settings*//
    public static final String READ_COMMUNITY = "public";
    public static final String WRITE_COMMUNITY= "private";
    String defaultOid = "1.3.6.1.2.1.1.1.0";
    String strIPAddress = "127.0.0.1";
    SNMP snmpController;

    //*Programm Settings*//
    boolean programmrunning = true;
    Main.Commands c = new Main.Commands();
    String[][] commands = {
            {"\nhelp"},
            {"\n\nget", "\t\t<IP-Address>", "\t<OID-Name>", "\n\t\t\t\t-v <Set Version> \n\t\t\t\t-port <Set Port> \n\t\t\t\t-c <Set Community> \n\t\t\t\t-n <Mib-File>"},
            {"\n\nset", "\t\t<IP-Address>", "\t<OID-Name>", "\n\t\t\t\t-v <Set Version> \n\t\t\t\t-port <Set Port> \n\t\t\t\t-c <Set Community> \n\t\t\t\t-string <Set Value> \n\t\t\t\t-integer <Set Value>"},
            {"\n\ndiscover", "\t<IP-Address>", "\n\t\t\t\t-v <Set Version> \n\t\t\t\t-port <Set Port> \n\t\t\t\t-c <Set Community> \n\t\t\t\t-m <Timeout in Min> \n\t\t\t\t-s <Timeout in Sek>"},
            {"\n\nmib"},
            {"\n\nshowmib", "\t\t<Name of MIB-File>"}
    };

    public Controller() throws IOException {
        snmpController = SNMP.getInstance();
    }


    public void startProgrammLoop() throws IOException {
        Scanner scanner = new Scanner(System.in);
        String[] input;
        String output = "";

        System.out.println("SNMP Program started:\n" +
                "Enter <help> to get all Commands.\n");
        while (programmrunning) {
            input = scanner.nextLine().split(" ");

            switch (input[0]) {
                case "help":
                    output = "All Commands:\n";
                    for (int i = 0; i < commands.length; i++) {
                        for (int j = 0; j < commands[i].length; j++) {
                            output += commands[i][j];
                        }
                    }
                    break;
                case "get":
                    snmpGet(input[1], input);
                    break;
                case "set":
                    snmpSet(input[1], input);
                    break;
                case "discover":
                    snmpDiscover(input);
                    break;
                case "mib":
                    int i=0;
                    String[] tmp = snmpController.getMibFileNames();
                    for(String str: tmp)
                        System.out.println(i++ + ")\t" + str);
                    break;
                case "showmib":
                    try {
                        MibSymbol[] mibSymbols= snmpController.getMibSymbols(input[1]);
                        for (MibSymbol v : mibSymbols)
                        {
                            if(v instanceof MibValueSymbol)
                                System.out.println("Name: " + v.getName() + "\t\tValue: " + ((MibValueSymbol)v).getValue());
                            else
                                System.out.println("Name: " + v.getName() + "\t\tValue: NULL");
                        }
                    } catch (MibLoaderException | IOException e) {
                        System.out.println("can not find file specified.\nTry to use the command <mib> to show all available mib files");
                    }
                    break;
                default:
                    output = "Unknown Command:\nType <help> to see all Commands.\n";
                    break;
            }
            System.out.println(output);
        }
    }


    public void snmpDiscover(String[] commands){
        int version = SnmpConstants.version1;
        int port = 161;
        String community = "public";
        long timeout = 5000;
        try {
            for (int i = 1; i < commands.length - 1; i += 2) {
                if (commands[i].toLowerCase().compareTo("-v") == 0) {
                    version = Integer.parseInt(commands[i + 1]);
                } else if (commands[i].toLowerCase().compareTo("-port") == 0) {
                    port = Integer.parseInt(commands[i + 1]);
                } else if (commands[i].toLowerCase().compareTo("-c") == 0) {
                    community = commands[i + 1];
                } else if (commands[i].toLowerCase().compareTo("-m") == 0) {
                    timeout = Integer.parseInt(commands[i+1]) * 60 * 1000;
                } else if (commands[i].toLowerCase().compareTo("-s") == 0) {
                    timeout = Integer.parseInt(commands[i+1]) * 1000;
                } else {
                    System.out.println("invalid input");
                    return;
                }
            }
        }
        catch(NullPointerException e)
        {
            System.out.println("wrong input. Enter help to see the available commands and how to use them");
            return;
        }

        //System.out.println("timeout" + timeout);
        LinkedList<Integer32> discoveryId = null;
        try {
            discoveryId = snmpController.discovery(community, version, timeout);
        } catch (IOException e) {
            e.printStackTrace();
        }

        long finalTimeout = timeout;
        LinkedList<Integer32> finalDiscoveryId = discoveryId;
        Runnable result = () -> {
            try {
                Thread.sleep(finalTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(Integer32 tmp : finalDiscoveryId)
            {
                ResponseEvent getResponse = snmpController.getResponse(tmp.toInt());
                if(getResponse != null)
                    System.out.println("discovered " + getResponse.getPeerAddress().toString() + ": " + getResponse.getResponse().getVariableBindings().iterator().next().getVariable());

            }

        };

        new Thread(result).start();
    }

    //by oid directly
    public void snmpGet(String strAddress, String community, String strOID)
    {
        String str="";
        try
        {
            OctetString community1 = new OctetString(community);
            strAddress= strAddress+"/" + 161;

            Address targetaddress = new UdpAddress(strAddress);

            TransportMapping transport = new DefaultUdpTransportMapping();
            transport.listen();

            CommunityTarget comtarget = new CommunityTarget();
            comtarget.setCommunity(community1);
            comtarget.setVersion(SnmpConstants.version1);
            comtarget.setAddress(targetaddress);
            comtarget.setRetries(2);
            comtarget.setTimeout(5000);

            PDU pdu = new PDU();
            ResponseEvent response;
            Snmp snmp;

            pdu.add(new VariableBinding(new OID(strOID)));
            pdu.setType(PDU.GET);

            snmp = new Snmp(transport);

            response = snmp.get(pdu,comtarget);

            if(response != null)
            {
                if(response.getResponse().getErrorStatusText().equalsIgnoreCase("Success"))
                {
                    PDU pduresponse=response.getResponse();
                    str=pduresponse.getVariableBindings().firstElement().toString();
                    if(str.contains("="))
                    {
                        int len = str.indexOf("=");
                        str=str.substring(len+1, str.length());
                    }
                }
            }
            else
            {
                System.out.println("no response from " + strIPAddress + "\ntry to use the command \"discovery\" to see all Devices in your network");
                return;
            }
            snmp.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        System.out.println("response from " + strIPAddress + ": " + strOID + " ==> " + str);
    }

    //by reading oid from MIB-File
    public void snmpGet(String ipAddress, String[] commands){
        InetAddress ip;
        int port = 161;
        String community = "public";
        OID oid = null;
        String strOID = null;
        int version = SnmpConstants.version1;
        try {
            ip = Inet4Address.getByName(ipAddress);

            for (int i = 3; i < commands.length - 1; i += 2) {
                if(commands[i].toLowerCase().compareTo("-o") == 0){
                    strOID = commands[i + 1];
                }else if (commands[i].toLowerCase().compareTo("-n") == 0) {
                    try {
                        oid = snmpController.getOidFromName(commands[2], commands[i + 1]);
                        //oid = snmp.getOidFromName(commands[2], "mibFiles\\ExtraMibs\\windows.mib");
                    } catch (MibLoaderException e) {
                        e.printStackTrace();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (commands[i].toLowerCase().compareTo("-v") == 0) {
                    version = Integer.parseInt(commands[i + 1]);
                } else if (commands[i].toLowerCase().compareTo("-port") == 0) {
                    port = Integer.parseInt(commands[i + 1]);
                } else if (commands[i].toLowerCase().compareTo("-c") == 0) {
                    community = commands[i + 1];
                } else {
                    System.out.println("invalid commands");
                    break;
                }
            }
        }
        catch(NullPointerException | UnknownHostException e)
        {
            System.out.println("wrong commands. Enter help to see the available commands and how to use them");
            return;
        }

        if(commands[2].toLowerCase().compareTo("-o") == 0) {
            strOID = commands[3];
        }

        //Get by oid directly
        if(strOID != null){
            snmpGet(ipAddress, community, strOID);
            return;
        }

        if(oid == null)
        {
            for(char c : commands[2].toCharArray())
            {
                if(Character.isLetter(c))
                {
                    System.out.println("invalid OID commands");
                    return;
                }
            }
            oid = new OID(commands[2]);
        }

        IpAddress addre = new UdpAddress(ip, port);
        ResponseEvent getResponse = null;
        try {
            getResponse = snmpController.get(addre, community, version, oid);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(getResponse == null || getResponse.getResponse() == null)
            System.out.println("no response from " + ip.getHostAddress() + "\ntry to use the command \"discovery\" to see all Devices in your network");
        else if(getResponse.getResponse().getErrorStatus() != 0)
            System.out.println("Error: " + getResponse.getResponse().getErrorStatus() );
        else
            System.out.println("response from " + getResponse.getPeerAddress().toString() + ": " + oid.toString() + " ==> " + getResponse.getResponse().getVariableBindings().iterator().next().getVariable());
    }


    //set by getting oid from mib
    public void snmpSet(String ipAddress, String commands[]){
        InetAddress ip;
        int port = 161;
        String community = "private";
        OID oid = null;
        int version = SnmpConstants.version1;
        Variable value = null;
        try {
            ip = Inet4Address.getByName(ipAddress);

            for (int i = 3; i < commands.length - 1; i += 2) {
                if (commands[i].toLowerCase().compareTo("-n") == 0) {
                    try {
                        oid = snmpController.getOidFromName(commands[2], commands[i + 1]);
                        //oid = snmp.getOidFromName(commands[2], "mibFiles\\ExtraMibs\\windows.mib");
                    } catch (MibLoaderException e) {
                        e.printStackTrace();
                        return;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (commands[i].toLowerCase().compareTo("-v") == 0) {
                    version = Integer.parseInt(commands[i + 1]);
                } else if (commands[i].toLowerCase().compareTo("-port") == 0) {
                    port = Integer.parseInt(commands[i + 1]);
                } else if (commands[i].toLowerCase().compareTo("-c") == 0) {
                    community = commands[i + 1];

                }
                else if(commands[i].toLowerCase().compareTo("-integer") == 0)
                {
                    if(value != null)
                    {
                        System.out.println("invalidInput: value already initialized");
                        break;
                    }
                    value = new Integer32(Integer.parseInt(commands[i + 1]));
                }
                else if(commands[i].toLowerCase().compareTo("-string") == 0)
                {
                    if(value != null)
                    {
                        System.out.println("invalidInput: value already initialized");
                        break;
                    }
                    value = new OctetString(commands[i + 1]);
                }
                else {
                    System.out.println("invalid input");
                    return;
                }
            }
        }
        catch(NullPointerException | UnknownHostException e)
        {
            System.out.println("wrong input. Enter help to see the available commands and how to use them");
            return;
        }

        if(oid == null)
        {
            for(char c : commands[2].toCharArray())
            {
                if(Character.isLetter(c))
                {
                    System.out.println("invalid OID input");
                    return;
                }
            }
            oid = new OID(commands[2]);
        }
        if(value == null)
        {
            System.out.println("value can not be empty, use the command \"help\" to see how to use this command");
            return;
        }

        IpAddress addre = new UdpAddress(ip, port);
        try {
            snmpController.set(addre, community, version, oid, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
