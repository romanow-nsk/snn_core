/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import java.io.BufferedReader;
import java.io.IOException;

/**
 *
 * @author romanow
 */
public class Token {
    private BufferedReader in;
    private String ss=null;
    public Token(BufferedReader in){
        this.in = in;
        }
    public Token(String ss){
        this.ss = ss;
        }
    public void nextLine() throws IOException{
        do  {
            ss= in.readLine();
            } while(ss==null || ss.charAt(0)=='/');        
        }
    public String get(){
        if (ss==null)
            return null;
        int idx = ss.indexOf("/");
        String out;
        if (idx==-1){
            out = ss;
            ss = null;
            return out;
            }
        out = ss.substring(0, idx);
        ss = ss.substring(idx+1);
        return out;
        }
    public int getInt(){
        String ss = get();
        return ss == null ? -1 : Integer.parseInt(ss);
        }
    public float getFloat(){
        String ss = get();
        return ss == null ? -1 : Float.parseFloat(ss);
        }
}
