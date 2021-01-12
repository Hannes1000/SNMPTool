package main.java;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
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

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

public class Controller {

    //*SNMP Settings*//
    public static final String READ_COMMUNITY = "public";
    public static final String WRITE_COMMUNITY= "private";
    String defaultOid = "1.3.6.1.2.1.1.1.0";
    String strIPAddress = "127.0.0.1";
    SNMP snmpController;

    //*Programm Settings*//
    boolean programmrunning = true;
    String[] input;
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

    //*FXML*//
    @FXML
    TextArea consoletxt;
    @FXML
    TextField consoleInput;


    public void consoleEnter(){
        input = consoleInput.getText().split(" ");
        handleInput();
    }

    public void helpEnter(){

    }

    public void getEnter(){

    }

    public void setEnter(){

    }

    public void discoverEnter(){

    }

    public void mibEnter(){

    }

    public void showmibEnter(){

    }

    public void handleInput(){
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> {
            String output = "";
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
                    output += snmpGet(input[1], input);
                    break;
                case "set":
                    output += snmpSet(input[1], input);
                    break;
                case "discover":
                    output += snmpDiscover(input);
                    break;
                case "mib":
                    int i=0;
                    String[] tmp = snmpController.getMibFileNames();
                    for(String str: tmp)
                        output += i++ + ")\t" + str;
                    break;
                case "showmib":
                    try {
                        MibSymbol[] mibSymbols= snmpController.getMibSymbols(input[1]);
                        for (MibSymbol v : mibSymbols)
                        {
                            if(v instanceof MibValueSymbol)
                                output += "Name: " + v.getName() + "\t\tValue: " + ((MibValueSymbol)v).getValue();
                            else
                                output += "Name: " + v.getName() + "\t\tValue: NULL";
                        }
                    } catch (MibLoaderException | IOException e) {
                        output += "can not find file specified.\nTry to use the command <mib> to show all available mib files";
                    }
                    break;
                default:
                    output = "Unknown Command:\nType <help> to see all Commands.\n";
                    break;
            }
            consoletxt.setText(consoletxt.getText() + output);
            consoleInput.setText("");
        });
    }


    //*FXML*//




    public void startProgrammLoop() throws IOException {
        Scanner scanner = new Scanner(System.in);
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


    public String snmpDiscover(String[] commands){
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
                    return "invalid input";
                }
            }
        }
        catch(NullPointerException e)
        {
            return "wrong input. Enter help to see the available commands and how to use them";
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
        ArrayList<String> str = new ArrayList<>();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(() -> {
            try {
                Thread.sleep(finalTimeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(Integer32 tmp : finalDiscoveryId)
            {
                ResponseEvent getResponse = snmpController.getResponse(tmp.toInt());
                if(getResponse != null) {
                    consoletxt.setText(consoletxt.getText() + "discovered " + getResponse.getPeerAddress().toString() + ": " + getResponse.getResponse().getVariableBindings().iterator().next().getVariable());
                    System.out.println("discovered " + getResponse.getPeerAddress().toString() + ": " + getResponse.getResponse().getVariableBindings().iterator().next().getVariable());
                }
            }

            });
        return "";
    }

    //by oid directly
    public String snmpGet(String strAddress, String community, String strOID)
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
                return "no response from " + strIPAddress + "\ntry to use the command \"discovery\" to see all Devices in your network";
            }
            snmp.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return "response from " + strIPAddress + ": " + strOID + " ==> " + str;
    }

    //by reading oid from MIB-File
    public String snmpGet(String ipAddress, String[] commands){
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
                        return "Error while loading MIB-File";
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
            return "wrong commands. Enter help to see the available commands and how to use them";
        }

        if(commands[2].toLowerCase().compareTo("-o") == 0) {
            strOID = commands[3];
        }

        //Get by oid directly
        if(strOID != null){
            snmpGet(ipAddress, community, strOID);
            return "";
        }

        if(oid == null)
        {
            for(char c : commands[2].toCharArray())
            {
                if(Character.isLetter(c))
                {
                    return "invalid OID commands";
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
            return "no response from " + ip.getHostAddress() + "\ntry to use the command \"discovery\" to see all Devices in your network";
        else if(getResponse.getResponse().getErrorStatus() != 0)
            return "Error: " + getResponse.getResponse().getErrorStatus() ;
        else
            return "response from " + getResponse.getPeerAddress().toString() + ": " + oid.toString() + " ==> " + getResponse.getResponse().getVariableBindings().iterator().next().getVariable();
    }


    //set by getting oid from mib
    public String snmpSet(String ipAddress, String commands[]){
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
                        return "Error while loading MIB";
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
                        return "invalidInput: value already initialized";
                    }
                    value = new Integer32(Integer.parseInt(commands[i + 1]));
                }
                else if(commands[i].toLowerCase().compareTo("-string") == 0)
                {
                    if(value != null)
                    {
                        break;
                    }
                    value = new OctetString(commands[i + 1]);
                }
                else {
                    return "invalid input";
                }
            }
        }
        catch(NullPointerException | UnknownHostException e)
        {
            return "wrong input. Enter help to see the available commands and how to use them";
        }

        if(oid == null)
        {
            for(char c : commands[2].toCharArray())
            {
                if(Character.isLetter(c))
                {
                    return "invalid OID input";
                }
            }
            oid = new OID(commands[2]);
        }
        if(value == null)
        {
            return "value can not be empty, use the command \"help\" to see how to use this command";
        }

        IpAddress addre = new UdpAddress(ip, port);
        try {
            snmpController.set(addre, community, version, oid, value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Successfully Set";
    }
}
