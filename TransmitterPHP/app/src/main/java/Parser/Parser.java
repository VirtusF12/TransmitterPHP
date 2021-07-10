package Parser;

import android.util.Log;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import PCodeDecodeMessage.CDMessage;

/**
 * Анализатор строк (форматы патернов показаны в Enum: FormatTextServer)
 * @author Mihail Kovalenko
 */
public final class Parser {

    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    private static final String TEG_Parser = "TEG_Parser";
    private ArrayList<String> innerList;

    public Parser(String text, FormatTextServer format){

        innerList = new ArrayList<>();
        execute(text, format);
    }

    public Parser(){

    }

    public String[] getArrayList(){

        return innerList.toArray(new String[innerList.size()]);
    }

    private void execute(String text, FormatTextServer f){

        String pattern = "";
        switch (f){

            case FORMAT1:
                pattern = "'\\w*'|('\\w*:\\w*:\\w*:\\w*:\\w*')"; // "'\\w*'|('\\w*:\\w*:\\w*:\\w*:\\w*')"
                innerList = parseInner(text, pattern);
                ;break;

            case FORMAT2:
                 pattern = "'\\w*'";
                innerList = parseInner(text, pattern);
                ;break;

            case FORMAT3:
                pattern = "'\\w*'|('\\w*:\\w*')";
                innerList = parseInner(text, pattern);
                ;break;

            case FORMAT4:
                pattern = "'\\w*,";
                innerList = parseInner(text, pattern);
                ;break;

            case FORMAT5: // php new format
                pattern = "'\\w*'";
                parseInnerVoid(text, pattern);
                ;break;

            case NONE:
                 innerList = parseInner(text);
                ;break;
        }
    }
    /**
     * Парсинг без патерна (сообщения)
     * @param text строку которую парсить
     * @return ArrayList<String>
     */
    private ArrayList<String> parseInner(String text){

        StringBuilder sb;
        ArrayList<String> list = new ArrayList<>();
        char[] arr = text.toCharArray();
        for (int i = 0; i < text.length(); i++){
            char current = arr[i];
            if (current == '\''){
                sb = new StringBuilder();
                int j = i + 1;
                while (arr[j] != '\'' && j < text.length()){
                    sb.append(arr[j]);
                    j++;
                }
                i = j;
                String nText = sb.toString();
                String nText1 = CDMessage.setKorToSpace(nText);
                String message = CDMessage.getMessage(nText1);
                list.add(message);
            }
        }
        return list;
    }
    /**
     * Парсинг по паттерну
     * @param text строку которую парсить
     * @param pattern парсинг с указанием патерна
     * @return ArrayList<String>
     */
    private ArrayList<String> parseInner(String text, String pattern){

        Pattern pat = Pattern.compile(pattern); // "'\\w*'"   "'\\w*'|('\\w*:\\w*)'"
        Matcher mat = pat.matcher(text);
        ArrayList<String> list = new ArrayList<>();
        while (mat.find()){
            list.add(new StringBuilder(mat.group()).substring(1,mat.group().length()-1));
        }
        return list;
    }

    private void parseInnerVoid(String text, String pattern){

        innerList.clear();
        Pattern pat = Pattern.compile(pattern); // "'\\w*'"   "'\\w*'|('\\w*:\\w*)'"
        Matcher mat = pat.matcher(text);

        while (mat.find()){
            innerList.add(new StringBuilder(mat.group()).substring(1,mat.group().length()-1));
        }
    }
    /**
     * Возврат списка распарсенного
     * @return ArrayList<String>
     */
    public ArrayList<String> getList(){
        return this.innerList;
    }
  //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    public String[] filterRegExprForUserName(String strUser){
        Pattern p = Pattern.compile("\\'([a-zA-z0-9]){2,}\\'");
        Matcher m = p.matcher(strUser);

        ArrayList<String> list = new ArrayList<>();
        while (m.find())
            list.add(strUser.substring(m.start()+1, m.end()-1));

        String[] res = list.toArray(new String[list.size()]);

        return res;
    }


    public void filterForMessages(String messages, ArrayList<String> list_mess){
        list_mess.clear();
        StringBuilder stringBuilder1;
        char[] arr = messages.toCharArray();
        for (int i = 0; i < messages.length(); i++){
            char current = arr[i];
            if (current == '\''){
                stringBuilder1 = new StringBuilder();
                int j = i + 1;
                while (arr[j] != '\'' && j < messages.length()){
                    stringBuilder1.append(arr[j]);
                    j++;
                }
                i = j;
                String nText = stringBuilder1.toString();
                Log.d(TEG_Parser, "nText=" + nText);
                String nText1 = CDMessage.setKorToSpace(nText);
                Log.d(TEG_Parser, "nText1=" + nText1);
                String message = CDMessage.getMessage(nText1);
                Log.d(TEG_Parser, "message=" + message);

                list_mess.add(message);
            }
        }
    }

    // используется для обработки канала и моих групп
    public void parseChanelGroup(String chanel, ArrayList<String> list_chanels){
        list_chanels.clear();
        StringBuilder sb;
        char[] arr = chanel.toCharArray();
        for (int i = 0; i < chanel.length(); i++){
            char current = arr[i];
            if (current == '\''){
                sb = new StringBuilder();
                int j = i + 1;
                while (arr[j] != '\'' && j < chanel.length()){
                    sb.append(arr[j]);
                    j++;
                }
                i = j;
                list_chanels.add(sb.toString());
            }
        }
    }


    public void parse0(String text, ArrayList<String> list, String pattern){

        Pattern pat = Pattern.compile(pattern);
        Matcher mat = pat.matcher(text);
        while (mat.find()){
            list.add(new StringBuilder(mat.group()).substring(1,mat.group().length()-1));
        }
    }

    public void parse1(String text, ArrayList<String> list){

        StringBuilder sb;
        char[] arr = text.toCharArray();
        for (int i = 0; i < text.length(); i++){
            char current = arr[i];
            if (current == '\''){
                sb = new StringBuilder();
                int j = i + 1;
                while (arr[j] != '\'' && j < text.length()){
                    sb.append(arr[j]);
                    j++;
                }
                i = j;
                list.add(CDMessage.getMessage(CDMessage.setKorToSpace(sb.toString())));
            }
        }
    }

    /**
     * @param text текст, который нужно проанализировать
     * @param list список куда необходимо его заполнить (проанализированный текст)
     */
    public void parse(String text, ArrayList<String> list){
        list.clear();
        StringBuilder sb;
        char[] arr = text.toCharArray();
        for (int i = 0; i < text.length(); i++){
            char current = arr[i];
            if (current == '\''){
                sb = new StringBuilder();
                int j = i + 1;
                while (arr[j] != '\'' && j < text.length()){
                    sb.append(arr[j]);
                    j++;
                }
                i = j;
                list.add(sb.toString());
            }
        }
    }

    /**
     * Метод класса - с применением RegExpr
     * @param answerServer строка от сервера
     * @return ArrayList<String>
     */
    /*
    public static ArrayList<String> parse$server(String answerServer){
        // (u'admin',)(u'dew',)(u'er1',)(u'lex1',)(u'lex2',)(u'mih',)(u'ter',)(u'test',)
        Pattern pat = Pattern.compile("'\\w*'"); // "'\\w*'"   "'\\w*'|('\\w*:\\w*)'"
        Matcher mat = pat.matcher(answerServer);

        ArrayList<String> list = new ArrayList<>();

        while (mat.find()){
            list.add(new StringBuilder(mat.group()).substring(1,mat.group().length()-1));
        }
        return list;
    }
    */
}
