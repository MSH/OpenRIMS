package com.github.binodnme.main;

import com.github.binodnme.dateconverter.converter.DateConverter;
import com.github.binodnme.dateconverter.utils.DateBS;

import java.util.Date;

/**
 * Created by binodnme
 * Created on 7/11/16.
 */
public class Main {
    /**
     * main method
     * @param args string args
     */
    public static void main(String[] args){
        DateBS dateBS = new DateBS();
        Date date = DateConverter.convertBSToAD(dateBS);
        System.out.println(date);
    }
}
