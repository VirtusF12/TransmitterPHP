package com.example.virtus.transmitter;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import HttpRequest.*;
import PCodeDecodeMessage.CDMessage;
import Parser.*;

import static HttpRequest.StateRequest.GET;
import static Parser.FormatTextServer.*;
/**
 * Класс - данные полученные от сервера для вложенного списка
 * @author Mihail Kovalenko
 */
public class ListDate {

    private static final String TEG_ListDate = "TEG_ListDate";
    private HttpRequest httpRequest;
    private ArrayList<String> listTheme, listThemJoin, listTask, listExec;

    /**
     * Внутренний парсер
     * @param text httpRequest от сервера
     * @param innerList список куда заносить данные
     * @param order какое действие указать task или exec
     */
    private void innerParserListDate(String text, ArrayList<String> innerList, final String order){

        switch (order){

            case "exec":
                String pattern = "'\\w*'";
                Pattern pat = Pattern.compile(pattern); // "'\\w*'"   "'\\w*'|('\\w*:\\w*)'"
                Matcher mat = pat.matcher(text);
                while (mat.find())
                    innerList.add(new StringBuilder(mat.group()).substring(1,mat.group().length()-1));
                ;break;

            case "task":
                StringBuilder stringBuilder1;
                char[] arr = text.toCharArray();
                for (int i = 0; i < text.length(); i++){
                    char current = arr[i];
                    if (current == '\''){
                        stringBuilder1 = new StringBuilder();
                        int j = i + 1;
                        while (arr[j] != '\'' && j < text.length()){
                            stringBuilder1.append(arr[j]);
                            j++;
                        }
                        i = j;
                        String nText = stringBuilder1.toString();
                        String nText1 = CDMessage.setKorToSpace(nText);
                        String message = CDMessage.getMessage(nText1);
                        innerList.add(message);
                    }
                }
                ;break;
        }
    }

    /**
     * инициализация списков: тем, заданий и выполнений
     * @param myLogin мой логин
     * @param context контекс
     */
    public ListDate(String myLogin, Context context){

        listTheme = new ArrayList<>();
        listExec = new ArrayList<>();
        listTask = new ArrayList<>();
        listThemJoin = new ArrayList<>();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        final String url = "http://195.133.146.149/?part=board_manage_task&action=get&detalAction=getListAuthorSubjectDateTask&user="+myLogin;
        httpRequest = new HttpRequest(url, GET, context);
        httpRequest.execute();
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Log.d(TEG_ListDate, "start");
        listTheme = new Parser(httpRequest.getRequest(), FORMAT1).getList(); // получили список тем author, subject, date

        Log.d(TEG_ListDate, "httpRequest = " + httpRequest.getRequest());
        Log.d(TEG_ListDate, "listTheme.size() = " + listTheme.size());
        for (String t : listTheme)
            Log.d(TEG_ListDate, "--> " + t);
        Log.d(TEG_ListDate, "stop");

        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        Log.d(TEG_ListDate, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ начало цикла получения данных");
        // добавлен поток для обработки диалогового окна
        Thread th = new Thread(new Runnable() {
            @Override
            public void run() {

                for (int i = 2; i < listTheme.size(); i+=3){

                    Log.d(TEG_ListDate, "**************************************\n\n");
                    String author = listTheme.get(i-2);
                    String subject = listTheme.get(i-1);
                    String date = listTheme.get(i);

                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    Log.d(TEG_ListDate, "i = " + i + ": (author = " + author + ", subject = " + subject + ", date = " + date + ")");
                    final String url$task = "http://195.133.146.149/?part=board_manage_task&action=get&detalAction=getListTask&user="+myLogin+"&author="+author+"&subject="+subject+"&date="+date;
                    httpRequest = new HttpRequest(url$task, GET, context);
                    httpRequest.execute();
                    Log.d(TEG_ListDate, "~~~ответ от сервера = " + httpRequest.getRequest());
                    // new Parser().filterForMessages(httpRequest.getRequest(), listTask);
                    innerParserListDate(httpRequest.getRequest(), listTask, "task");
                    Log.d(TEG_ListDate, "listTask.size() = " + listTask.size());
                    listTask.add("stop");
                    for (String t : listTask)
                        Log.d(TEG_ListDate, "--> " + t);
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~

                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    final String url$exec = "http://195.133.146.149/?part=board_manage_task&action=get&detalAction=getListTaskExec&user="+myLogin+"&author="+author+"&subject="+subject+"&date="+date;
                    httpRequest = new HttpRequest(url$exec, GET, context);
                    httpRequest.execute();
                    Log.d(TEG_ListDate, "~~~ответ от сервера = " + httpRequest.getRequest());
                    // new Parser().filterForMessages(httpRequest.getRequest(), listExec);
                    // listExec = new Parser(httpRequest.getRequest(), FORMAT5).getList();
                    innerParserListDate(httpRequest.getRequest(), listExec, "exec");
                    Log.d(TEG_ListDate, "listExec.size() = " + listExec.size());
                    listExec.add("stop");
                    for (String t : listExec)
                        Log.d(TEG_ListDate, "--> " + t);
                    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                    Log.d(TEG_ListDate, "**************************************\n\n");
                }
            }
        });
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (int i = 2; i < listTheme.size(); i+=3){
            String theme = listTheme.get(i-2) + " " + listTheme.get(i-1) + " " + listTheme.get(i);
            listThemJoin.add(theme);
        }

        for (String task : listTask)
            Log.d(TEG_ListDate, "-------> " + task);
        Log.d(TEG_ListDate, "```````````listTask.size() == " + listTask.size());
        for (String r : listExec)
            Log.d(TEG_ListDate, "------->>> " + r);
        Log.d(TEG_ListDate, "listExec.size() == " + listExec.size());
        Log.d(TEG_ListDate, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ конец цикла получения данных");
    }

    public ArrayList<String> getListTask(){
        return listTask;
    }

    public ArrayList<String> getListExec(){
        return listExec;
    }

    public ArrayList<String> getListThemJoin(){
        return listThemJoin;
    }

    /**
     * Заполение hashMaps в классе ExpandableListActivity
     * @param expListDetail hashMap для заданий: key -> тема value -> название задания
     * @param expListFlag hashMap для установки выполнения: key -> тема value -> "0" или "1"
     */
    public void setExpendableList(HashMap<String, ArrayList<String>> expListDetail, HashMap<String, ArrayList<Boolean>> expListFlag) {

        int j = 0;
        for (int i = 0; i < listThemJoin.size(); i++) {
            String them = listThemJoin.get(i);
            ArrayList<String> list_task = new ArrayList<>();
            ArrayList<Boolean> list_flag = new ArrayList<>();
            if (listTask.get(j).equals("stop"))
                j++;
            while (!listTask.get(j).equals("stop")){
                list_task.add(listTask.get(j));
                if (listExec.get(j).equals("0"))
                    list_flag.add(false);
                else
                    list_flag.add(true);
                j++;
            }
            expListDetail.put(them, list_task);
            expListFlag.put(them, list_flag);
        }
    }
}
