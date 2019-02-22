package utils;

import i.Sendcallback;

/**
 * Created by admin on 2017-7-18.
 */

public class CallBackHelp {
    public static Sendcallback sendcallback;

    public static void setSendMessage(Sendcallback sendcallback) {
        CallBackHelp.sendcallback = sendcallback;
    }

    public static Sendcallback getMessage(){
        return sendcallback;
    }

    public static void  cancelMessage(Sendcallback sendcallback){
        CallBackHelp.sendcallback = null;
    }
}
