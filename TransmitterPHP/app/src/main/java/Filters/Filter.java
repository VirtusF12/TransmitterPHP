package Filters;


import PCodeDecodeMessage.CDMessage;
import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Filter {

    public  String filterSpace(String str){
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < str.length();i++){
            char symb = str.charAt(i);
            if (symb != ' '){
                stringBuilder.append(symb);
            }
        }
        String res = stringBuilder.toString();
        return res;
    }

    private String setNoneSpaceToMessage(String message){
        return message.replace('`',' ');
    }

    public String[] filterRegExprForUserName(String strUser){
        Pattern p = Pattern.compile("\\'([a-zA-z0-9]){2,}\\'");
        Matcher m = p.matcher(strUser);

        ArrayList<String> list = new ArrayList<>();
        while (m.find())
            list.add(strUser.substring(m.start()+1, m.end()-1));

        String[] res = list.toArray(new String[list.size()]);

        return res;
    }
}
