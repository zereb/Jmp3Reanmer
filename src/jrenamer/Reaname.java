/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jrenamer;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.InvalidDataException;
import com.mpatric.mp3agic.Mp3File;
import com.mpatric.mp3agic.UnsupportedTagException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.mentaregex.Regex.*;

/**
 *
 * @author qqqq
 */
public class Reaname extends Observable implements Runnable {

    //private ArrayList<File> fileList = new ArrayList<File>();
    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    static Random rnGesus = new Random();
    public Object[][] data;
    private Mp3File mp3file;
    private Thread t;
    private  String prefix;
    private ArrayList<File> fileList;
    
    Map<String, Integer> hm = new HashMap<String, Integer>();
    
    
    public float progress=1;
    public int totalFiles;

    public String randomString(int len) {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(AB.charAt(rnGesus.nextInt(AB.length())));
        }
        return sb.toString();
    }

    public Reaname(ArrayList<File> fileList, String prefix) {
        hm.put("{ARTIST}", 2);
        hm.put("{PREFIX}", 1);
        hm.put("{TITLE}", 3);
        hm.put("{RANDOM}", 4);
        
        this.fileList=fileList;
        this.prefix=prefix;
        data = new Object[fileList.size()][6];
        totalFiles = fileList.size();
        t = new Thread(this);
        t.start();

    }

    public Object getData() {
        return data;
    }

    public void run() {
        int i = -1;
        for (File fl : fileList) {
            Log.putInfo("trying to rename: " + fl.getAbsolutePath());
            String path = fl.getAbsolutePath().substring(0, fl.getAbsolutePath().indexOf(fl.getName()));
            i++;
            try {
                mp3file = new Mp3File(fl);
                Log.putInfo("Length of this mp3 is: " + mp3file.getLengthInSeconds() + " seconds");
                Log.putInfo("Bitrate: " + mp3file.getLengthInSeconds() + " kbps " + (mp3file.isVbr() ? "(VBR)" : "(CBR)"));
                Log.putInfo("Sample rate: " + mp3file.getSampleRate() + " Hz");
                Log.putInfo("Has ID3v1 tag?: " + (mp3file.hasId3v1Tag() ? "YES" : "NO"));
                Log.putInfo("Has ID3v2 tag?: " + (mp3file.hasId3v2Tag() ? "YES" : "NO"));
                Log.putInfo("Has custom tag?: " + (mp3file.hasCustomTag() ? "YES" : "NO"));

                data[i][0] = fl.getName();
                data[i][1] = prefix;
                if (mp3file.hasId3v1Tag()) {
                    ID3v1 id3v1Tag = mp3file.getId3v1Tag();
                    data[i][2] = id3v1Tag.getArtist();
                    data[i][3] = id3v1Tag.getTitle();
                } else if (mp3file.hasId3v2Tag()) {
                    ID3v2 id3v2Tag = mp3file.getId3v2Tag();
                    data[i][2] = id3v2Tag.getArtist();
                    data[i][3] = id3v2Tag.getTitle();
                }
                data[i][4]=randomString(4);
                
                progress=1+i;
                Log.putDebug(progress+"% "+i+"/"+totalFiles);
                stateChanged();
                t.sleep(1);
               

            } catch (IOException ex) {
                Log.putDebug(ex.toString());
            } catch (UnsupportedTagException ex) {
                Log.putDebug(ex.toString());
            } catch (InvalidDataException ex) {
                Log.putDebug(ex.toString());
            } catch (InterruptedException ex) {
                Log.putDebug(ex.toString());
            }
        }

    }

    public boolean doReaname(Object[][] data, String rule){
        int i=-1;
        String[] commands=match(rule, Constants.REGEX);
        for (String command : commands) {
            Log.putInfo(command);
        }

        
        
        for (File fl: fileList) {
            try {
                i++;
                String path = fl.getAbsolutePath().substring(0, fl.getAbsolutePath().indexOf(fl.getName()));
                String format = fl.getAbsolutePath().substring(fl.getAbsolutePath().length()-3);
                String newName=path;
                for (String command : commands) {
                    
                    rule.replaceAll(command, (String) data[i][hm.get(command)]);
//                  newName=path+data[i][1]+data[i][2]+" - "+data[i][3]+"."+format;
//                    newName=newName+data[i][hm.get(command)];
                }
                newName=rule+format;
                
                fl.renameTo(new File(newName));
                Log.putInfo(newName+" renamed");
            } catch (Exception e) {
                Log.putDebug(e.toString());
                e.printStackTrace();
                return  false;
            }
        }
        return  true;
    }
    private void stateChanged() {
        setChanged();
        notifyObservers();
    }
}
