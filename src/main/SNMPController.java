package main;


public class SNMPController {
    public void snmpSet(String strAddress, String community, String strOID, int Value)
    {
        strAddress= strAddress+"/"+161;
    }


    public String snmpGet(String strAddress, String community, String strOID)
    {
        String str="";
        return str;

    }
}
