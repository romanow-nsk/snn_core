/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

/**
 *
 * @author romanow
 */
public class Static {
    public static String put(float val){
        String ss = ""+val;
        int ii=ss.indexOf(".");
        if (ii== -1) return ss;
        ii+=3;
        if (ii>=ss.length())
            return ss;
        return ss.substring(0,ii);
        }
}
