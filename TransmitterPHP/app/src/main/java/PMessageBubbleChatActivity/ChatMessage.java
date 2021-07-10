package PMessageBubbleChatActivity;


public class ChatMessage {



    public String login, time, mess, rellLogin;

    public ChatMessage(String login, String time, String mess, String rellLogin){

        this.login = login;
        this.time = time;
        this.mess = mess;
        this.rellLogin = rellLogin;
    }

    public String getMessage(){

        String result = login + "  " + time + "\n" + mess;
        return result;
    }

    public boolean positionBubbleMess(){

        if (rellLogin.equals(login))
            return  true;
        else
            return false;
    }
}


// старый код
//    public boolean left;
//    public String message;

//    public ChatMessage(boolean left, String message) {
//        super();
//        this.left = left;
//        this.message = message;
//    }