package main;

import test.Test;

public class Main {

    public static final String READ_COMMUNITY = "public";

    public static final String WRITE_COMMUNITY= "private";

    public static final int mSNMPVersion = 0; // 0 represents SNMPController version=1

    public static final String OID_UPS_OUTLET_GROUP1 =
            "1.3.6.1.4.1.318.1.1.1.12.3.2.1.3.1";

    public static final String OID_SYS_DESCR="1.3.6.1.2.1.1.1.0";


    public static void main(String[] args) {

        String strIPAddress = "127.0.0.1";
        SNMPController snmpController = new SNMPController();
        //Set Value=2 to trun OFF UPS OUTLET Group1
        //Value=1 to trun ON UPS OUTLET Group1
        int Value = 2;

        try
        {
            //snmpController.snmpSet(strIPAddress, WRITE_COMMUNITY,OID_UPS_OUTLET_GROUP1, Value);
            //snmpController.snmpSet();

            String batteryCap = snmpController.snmpGet(strIPAddress, READ_COMMUNITY, OID_SYS_DESCR);
            //System.out.println(batteryCap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
