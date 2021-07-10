package PCodeDecodeMessage;

import ASCII.ASCII;

public final class CDMessage {

    public static String CodeMessage(String message){
        String newStr = "";
        for (int i = 0; i < message.length();i++){
            char currSimb = message.charAt(i);
            for(int j = 0; j < ASCII.getSizeTableASCII(); j++){
                if (currSimb == ASCII.token.get(j)){
                    newStr += Integer.toString(j) + "`";
                    break;
                }
            }
        }
        return newStr.substring(0, newStr.length()-1);
    }

    public static String setKorToSpace(String message){
        return message.replace('`',' ');
    }

    private static int[] ConvertStringToIntArray(String[] arr){
        int[] arrInt = new int[arr.length];
        for (int i = 0; i < arr.length; i++)
            if (Integer.parseInt(arr[i]) == 128)
                arrInt[i] = 144;
            else
                arrInt[i] = Integer.parseInt(arr[i]);
        return arrInt;
    }

    public static String getMessage(String mess){
        String[] strNumbers = mess.split(" ");
        int[] intNumvbers = ConvertStringToIntArray(strNumbers);

        String res = "";
        for (int i = 0; i < intNumvbers.length; i++){
            int currentValue = intNumvbers[i];
            res += ASCII.token.get(currentValue);
        }
        return res;
    }
}
