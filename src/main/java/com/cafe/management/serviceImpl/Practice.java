package com.cafe.management.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Practice {

    public List<String> addingToString(){
        List<String> inputList = new ArrayList<>();
        inputList.add("Abc");
        inputList.add("Def");
        inputList.add("Ghi");
        inputList.add("Jkl");
        inputList.add("Mno");
        inputList.add("Pqr");
        inputList.add("Stu");
        inputList.add("Vwx");
        inputList.add("Yz");
        List<String> newList = new ArrayList<>();
        for (String inputString : inputList){
            char[] stringsToChar = inputString.toCharArray();
            for (char output : stringsToChar){
                String outputString = output + " - Alpha";
                newList.add(outputString);
            }
        }
        return newList;
    }

    public static void main(String[] args) {
        Practice practice = new Practice();
        System.out.println(practice.addingToString());
    }
}
