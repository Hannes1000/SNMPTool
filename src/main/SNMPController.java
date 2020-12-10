package main;

import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class SNMPController {

    public void snmpSet(String strAddress, String community, String strOID, int Value)
    {
        strAddress= strAddress+"/"+161;

        Address targetAddress = GenericAddress.parse(strAddress);
        Snmp snmp;

        try
        {
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            transport.listen();

            CommunityTarget target = new CommunityTarget();
            target.setCommunity(new OctetString(community));
            target.setAddress(targetAddress);
            target.setRetries(2);
            target.setTimeout(5000);
            target.setVersion(SnmpConstants.version1);

            PDU pdu = new PDU();
            pdu.add(new VariableBinding(new OID(strOID), new Integer32(Value)));
            pdu.setType(PDU.SET);

            ResponseListener listener = new ResponseListener() {
                public void onResponse(ResponseEvent event) {
                    ((Snmp)event.getSource()).cancel(event.getRequest(), this);
                    System.out.println("Set Status is:"+event.getResponse().getErrorStatusText());
                }
            };

            snmp.send(pdu, target, null, listener);
            snmp.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

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
                System.out.println("Feeling like a TimeOut occured ");
            }
            snmp.close();

        } catch(Exception e) {
            e.printStackTrace();
        }

        return str;

    }
}
