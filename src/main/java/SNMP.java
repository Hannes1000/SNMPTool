package main.java;

import net.percederberg.mibble.*;
import net.percederberg.mibble.value.ObjectIdentifierValue;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.util.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;

public class SNMP implements CommandResponder, ResponseListener {

    private static SNMP instance;

    static {
        try {
            instance = new SNMP();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MibLoader mibLoader;
    private Snmp snmp;
    private static final String STANDARD_IANA_MIB_DIRECTORY = "mibFiles\\mibs\\iana";
    private static final String STANDARD_IETF_MIB_DIRECTORY = "mibFiles\\mibs\\ietf";
    private static final String EXTRA_MIB_DIRECTORY = "mibFiles\\ExtraMibs";
    private MultiThreadedMessageDispatcher dispatcher;
    private Address listenAddress;
    private ThreadPool threadPool;

    LinkedList<ResponseEvent> asyncResponseDeposit = new LinkedList<ResponseEvent>();
    //HashMap<Integer32, ResponseEvent> asyncResponseDeposit = new HashMap<Integer32, ResponseEvent>();

    public SNMP() throws IOException {
        mibLoader = new MibLoader();
        mibLoader.addDir(new File(STANDARD_IANA_MIB_DIRECTORY));
        mibLoader.addDir(new File(STANDARD_IETF_MIB_DIRECTORY));
        mibLoader.addDir(new File(EXTRA_MIB_DIRECTORY));

        threadPool = ThreadPool.create("Trap", 10);
        dispatcher = new MultiThreadedMessageDispatcher(threadPool,
                new MessageDispatcherImpl());

        //TRANSPORT
        listenAddress = new UdpAddress(Inet4Address.getByName("0.0.0.0"), 162);

        snmp = new Snmp(dispatcher, new DefaultUdpTransportMapping((UdpAddress) listenAddress));
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
        snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());

        snmp.listen();
        snmp.addCommandResponder(this);
    }

    public static SNMP getInstance() { return instance; }


    public ResponseEvent get(IpAddress ip, String community, int version, OID oid) throws IOException {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(ip);
        target.setVersion(version);

        //snmp.
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);
        pdu.add(new VariableBinding(oid));

        return snmp.send(pdu, target);
    }

    public Integer32 getAsync(IpAddress ip, String community, int version, OID oid) throws IOException {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(ip);
        target.setVersion(version);

        //snmp.
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);
        pdu.add(new VariableBinding(oid));

        snmp.send(pdu, target, null, this);
        return pdu.getRequestID();
    }

    public Integer32 getAsync(IpAddress ip, String community, int version, OID oid, long timeout) throws IOException {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(ip);
        target.setVersion(version);
        target.setTimeout(timeout);

        //snmp.
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);
        pdu.add(new VariableBinding(oid));

        snmp.send(pdu, target, null, this);
        return pdu.getRequestID();
    }

    /*public ResponseEvent getMultipleAsync() //for broadcast
    {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(ip);
        target.setVersion(version);

        //snmp.
        PDU pdu = new PDU();
        pdu.setType(PDU.GET);
        pdu.add(new VariableBinding(oid));

        snmp.send(pdu, target, null, this);
    }*/

    public void set(IpAddress ip, String community, int version, OID oid, Variable value) throws IOException {
        CommunityTarget target = new CommunityTarget();
        target.setAddress(ip);
        target.setCommunity(new OctetString(community));
        target.setVersion(version);

        PDU pdu = new PDU();
        pdu.setType(PDU.SET);
        pdu.add(new VariableBinding(oid, value));

        snmp.set(pdu, target);
    }

    public ResponseEvent getNext(IpAddress ip, String community, int version, OID oid) throws IOException
    {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(ip);
        target.setVersion(version);

        //snmp.
        PDU pdu = new PDU();
        pdu.setType(PDU.GETNEXT);
        pdu.add(new VariableBinding(oid));

        return snmp.getNext(pdu, target);
    }

    public Integer32 getNextAsync(IpAddress ip, String community, int version, OID oid) throws IOException {
        CommunityTarget target = new CommunityTarget();
        target.setCommunity(new OctetString(community));
        target.setAddress(ip);
        target.setVersion(version);

        //snmp.
        PDU pdu = new PDU();
        pdu.setType(PDU.GETNEXT);
        pdu.add(new VariableBinding(oid));

        snmp.send(pdu, target, null, this);
        return pdu.getRequestID();
    }

    public LinkedList<Integer32> discovery(String community, int version) throws IOException {

        //Converte Ip address to an int
        String[] ipString = InetAddress.getLocalHost().getHostAddress().split("\\.");
        int[] ip = new int[4];
        for(int i=0; i<4; i++)
            ip[i] = Byte.parseByte(ipString[i]);
        int byteIp = 0;
        for(int i=0; i<4; i++)
        {
            byteIp = byteIp << 8;
            byteIp |= ip[i];
        }

        //get SubnetMask
        int subnetMask = NetworkInterface.getByInetAddress(Inet4Address.getLocalHost()).getInterfaceAddresses().get(0).getNetworkPrefixLength();
        //calculate hoe many address there can be in the network
        int amountOfAddress = (int) Math.pow(2, 32 - subnetMask);

        //Set x bits to 0
        byteIp >>= 32 - subnetMask;
        byteIp <<= 32 - subnetMask;

        LinkedList<Integer32> ret = new LinkedList<Integer32>();
        String ipStr;
        int tmp;

        //calculate every possible ip in network and send a getRequest
        for(int i=0; i<amountOfAddress; i++)
        {
            tmp = byteIp;
            for(int x=3; x>=0; x--)
            {
                ip[x] = (short) tmp;
                ip[x] &= 0xFF;
                tmp >>= 8;
            }
            ipStr = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
            UdpAddress address = new UdpAddress(InetAddress.getByName(ipStr), 161);
            ret.add(this.getAsync(address, community, version, new OID("1.3.6.1.2.1.1.5.0")));
            byteIp++;
        }

        return ret;
    }

    public LinkedList<Integer32> discovery(String community, int version, long delay) throws IOException {

        //Converte Ip address to an int
        String[] ipString = InetAddress.getLocalHost().getHostAddress().split("\\.");
        int[] ip = new int[4];
        for(int i=0; i<4; i++)
            ip[i] = Integer.parseInt(ipString[i]);
        int byteIp = 0;
        for(int i=0; i<4; i++)
        {
            byteIp = byteIp << 8;
            byteIp |= ip[i];
        }

        //get SubnetMask
        int subnetMask = NetworkInterface.getByInetAddress(Inet4Address.getLocalHost()).getInterfaceAddresses().get(0).getNetworkPrefixLength();
        //calculate hoe many address there can be in the network
        int amountOfAddress = (int) Math.pow(2, 32 - subnetMask);

        //Set x bits to 0
        byteIp >>= 32 - subnetMask;
        byteIp <<= 32 - subnetMask;

        LinkedList<Integer32> ret = new LinkedList<Integer32>();
        String ipStr;
        int tmp;

        //calculate every possible ip in network and send a getRequest
        for(int i=0; i<amountOfAddress; i++)
        {
            tmp = byteIp;
            for(int x=3; x>=0; x--)
            {
                ip[x] = (short) tmp;
                ip[x] &= 0xFF;
                tmp >>= 8;
            }
            ipStr = ip[0] + "." + ip[1] + "." + ip[2] + "." + ip[3];
            UdpAddress address = new UdpAddress(InetAddress.getByName(ipStr), 161);
            ret.add(this.getAsync(address, community, version, new OID("1.3.6.1.2.1.1.5.0"), delay));
            byteIp++;
        }

        return ret;
    }

    public Integer32 discoveryBroadcast(String community, int version) throws IOException
    {
        return getAsync(new UdpAddress(Inet4Address.getByName("255.255.255.255"), 161), community, version, new OID("1.3.6.1.2.1.1.5.0")); //OID for name
    }

    public void close() throws IOException {
        snmp.close();
    }

    public void onResponse(ResponseEvent responseEvent) {
        synchronized (asyncResponseDeposit)
        {
            if(responseEvent != null)
                this.asyncResponseDeposit.add(responseEvent);
                //this.asyncResponseDeposit.put(responseEvent.getResponse().getRequestID(), responseEvent);
            else
                System.out.println("unknown message received");
        }

    }

    public ResponseEvent getResponse(int id)
    {
        synchronized (asyncResponseDeposit)
        {
            for(int i=0; i<asyncResponseDeposit.size(); i++)
            {
                if(asyncResponseDeposit.get(i).getResponse() != null && asyncResponseDeposit.get(i).getResponse().getRequestID().toInt() == id)
                {
                    ResponseEvent ret = asyncResponseDeposit.get(i);
                    asyncResponseDeposit.remove(i);
                    return ret;
                }
            }
        }

        return null;
    }

    public void clearResponse() { this.asyncResponseDeposit = new LinkedList<ResponseEvent>(); }

    public OID getOidFromName(String name, String mibName) throws IOException, MibLoaderException {
        Mib mib = mibLoader.load(new File(EXTRA_MIB_DIRECTORY + "\\" + mibName));

        MibValue value = ((MibValueSymbol) mib.getSymbol(name)).getValue();
        OID ret = new OID(((ObjectIdentifierValue) value).toString()+ ".0"); //geht sonst nicht

        mibLoader.unload(mibName);
        return ret;
    }

    public void processPdu(CommandResponderEvent crEvent) {
        PDU pdu = crEvent.getPDU();
        if (pdu.getType() == PDU.V1TRAP) {

            PDUv1 pduV1 = (PDUv1) pdu;
            System.out.println("");

            System.out.println("===== NEW SNMP 1 TRAP RECEIVED ====");
            System.out.println("agentAddr " + pduV1.getAgentAddress().toString());
            System.out.println("enterprise " + pduV1.getEnterprise().toString());
            System.out.println("timeStam" + pduV1.getTimestamp());
            System.out.println("snmpVersion 1");
            System.out.println("communityString " + new String(crEvent.getSecurityName()));


        } else if (pdu.getType() == PDU.TRAP) {
            System.out.println("");
            System.out.println("===== NEW SNMP 2 TRAP RECEIVED ====");

            System.out.println("errorStatus " + String.valueOf(pdu.getErrorStatus()));
            System.out.println("errorIndex "+ String.valueOf(pdu.getErrorIndex()));
            System.out.println("requestID " +String.valueOf(pdu.getRequestID()));
            System.out.println("snmpVersion 2");
            System.out.println("communityString " + new String(crEvent.getSecurityName()));

        }

        Vector<? extends VariableBinding> varBinds = pdu.getVariableBindings();
        if (varBinds != null && !varBinds.isEmpty()) {
            Iterator<? extends VariableBinding> varIter = varBinds.iterator();

            StringBuilder resultset = new StringBuilder();
            resultset.append("-----");
            while (varIter.hasNext()) {
                VariableBinding vb = varIter.next();

                String syntaxstr = vb.getVariable().getSyntaxString();
                int syntax = vb.getVariable().getSyntax();
                System.out.println( "OID: " + vb.getOid());
                System.out.println("Value: " +vb.getVariable());
                System.out.println("syntaxstring: " + syntaxstr );
                System.out.println("syntax: " + syntax);
                System.out.println("------");
            }

        }
        System.out.println("==== TRAP END ===");
        System.out.println("");
    }

    public MibSymbol[] getMibSymbols(String mibFileName) throws IOException, MibLoaderException {
        Collection<MibSymbol> collection = null;
        collection = mibLoader.load(new File(EXTRA_MIB_DIRECTORY + "\\" + mibFileName)).getAllSymbols();

        MibSymbol[] ret = new MibSymbol[collection.size()];
        int i=0;
        for(MibSymbol m : collection)
            ret[i++] = m;

        mibLoader.unload(mibFileName);
        return ret;
    }

    public String[] getMibFileNames()
    {
        File[] fp = (new File(EXTRA_MIB_DIRECTORY)).listFiles();
        String[] ret = new String[fp.length];
        for(int i=0; i<fp.length; i++)
            ret[i] = fp[i].getName();
        return ret;
    }
}
