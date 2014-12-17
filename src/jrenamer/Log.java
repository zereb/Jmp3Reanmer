/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jrenamer;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author qqqq
 */
public final class Log {
    private static List<String> log = new ArrayList<String>();
    private static List<String> debug = new ArrayList<String>();
    public static int index=-1;
    public static int indexD=-1;
    
    public static void putInfo(String x){
        log.add(x);
        index++;
        System.out.println(index+": "+log.get(index));
    }
    
    public static void putDebug(String x){
        debug.add(x);
        indexD++;
        System.err.println(indexD+": "+debug.get(indexD));
    }
    
    public static String getInfoLog(){
        if(index>-1)
            return log.get(index);
        return  null;
    }
    public static String getDebugLog(){
        return debug.get(indexD);
    }
}
