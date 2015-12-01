package com.robertsapps.android.bluetoothrover;

/**
 * Created by Robert on 15/09/2015.
 */
public class Series50 {

    //region Variables
    static int packagestatus = 0;
    static char DLE = 0x10;
    static char STX = 0x02;
    static char ETX = 0x03;
    //endregion

    //region Public Functions
    public static String parse(char data) {

        StringBuilder sb = new StringBuilder();

        if (packagestatus == 0) //expecting DLE
        {
            sb.setLength(0);
            if (data == DLE) {
                packagestatus = 1;
            } else {
                packagestatus = 0;
            }
        } else if (packagestatus == 1) //expecting STX
        {
            if (data == STX) {
                packagestatus = 2;
            } else {
                packagestatus = 0;
            }

        } else if (packagestatus == 2) //expecting data
        {
            sb.append(data);
            if (data == DLE)
                packagestatus = 3;
        } else if (packagestatus == 3)
        {
            if (data == DLE) //DLE is part of data package
            {
                packagestatus = 2;
            } else if (data == ETX) //end of package
            {
                sb.setLength(sb.length() - 1);
                packagestatus = 0;
                return sb.toString();
            } else if (data == STX) //new package has started ?
            {
                sb.setLength(0);
                packagestatus = 2;
            }
        }
        return null;
    }

    public static String construct(String series50message) {

        StringBuilder sb = new StringBuilder();

        sb.append(DLE);
        sb.append(STX);
        sb.append(series50message);
        sb.append(DLE);
        sb.append(ETX);

        return sb.toString();
    }

    public static String process(String series50message) //Process parsed data.
    {
        byte[] message = series50message.getBytes();
        switch(message[0])
        {
            case 'C': // CBlock
                //TODO handle C message
                break;
            case 'X': // Patch Status
                //TODO handle X message
                break;
            case 'Z': // Electrode Status
                //TODO handle Z message
                break;
            case 'b': // Bypass Impedance Acknowledge
                //TODO handle b message
                break;
            case 'm': // Change Mode acknowledge
                //TODO handle F message
            case 'k': // Mode status
                //TODO handle X message acknowledgement
                break;
            case 'd': //Firmware data Package acknowledge
                //TODO handle Z message acknowledgement
                break;
            case 'f': // Upgrade status
                //TODO handle f message
                break;
            case 'r': // Ready to Receive data firmware mode
                //TODO handle r messaage
                break;
            default:
                break;
        }

        return null;
    }
    //endregion

    //region Private Functions
    private static String handle_C(byte[] cblock)
    {
        return "Command not implemented";
    }

    private static String handle_X(byte[] patchstatus)
    {
        return "Command not implemented";
    }

    private static String handle_Z(byte[] impedancestatus)
    {
        return "Command not implemented";
    }

    private static String handle_b(byte[] status)
    {
        return "Command not implemented";
    }
    private static String handle_m(byte[] mode)
    {
        return "Command not implemented";
    }

    private static String handle_k(byte[] mode)
    {
        return "Command not implemented";
    }

    private static String handle_d(byte[] status)
    {
        return "Command not implemented";
    }

    private static String handle_f(byte[] status)
    {
        return "Command not implemented";
    }

    private static String r()
    {
        return "Command not implemented";
    }

    private static String send_M(byte mode)
    {
        StringBuilder sb = new StringBuilder();
        sb.append('M');
        sb.append(mode);
        construct(sb.toString());

        return "Command not implemented";
    }

    private static String send_K()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('K');
        //sb.append( CURRENT DEVICE MODE);
        construct(sb.toString());

        return "Command not implemented";
    }

    private static String send_B(byte status)
    {
        StringBuilder sb = new StringBuilder();
        sb.append('B');
        sb.append(status);
        construct(sb.toString());

        return "Command not implemented";
    }

    private static String send_D(byte[] dataPacket)
    {
        StringBuilder sb = new StringBuilder();
        sb.append('D');
        for (byte element: dataPacket)
            sb.append(element);
        construct(sb.toString());

        return "Command not implemented";
    }

    private static String send_F()
    {
        StringBuilder sb = new StringBuilder();
        sb.append('F');
        construct(sb.toString());

        return "Command not implemented";
    }

    private static String send_x(byte status)
    {
        StringBuilder sb = new StringBuilder();
        sb.append('D');
        sb.append(status);
        construct(sb.toString());

        return "Command not implemented";
    }

    private static String send_z(byte[] status)
    {
        StringBuilder sb = new StringBuilder();
        sb.append('D');
        for (byte element: status)
            sb.append(element);
        construct(sb.toString());

        return "Command not implemented";
    }
    //endregion
}