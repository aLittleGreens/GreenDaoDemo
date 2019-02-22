package bean;

/**
 * Created by admin on 2017-7-18.
 */

public class Event {

    public static class SendEvent{

        public String msg;
        public SendEvent(String msg){
            this.msg = msg;
        }
    }

    public static class ReceiveEvent{
        public String msg;
        public ReceiveEvent(String msg){
            this.msg = msg;
        }
    }
}
