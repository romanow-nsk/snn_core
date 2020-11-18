/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package romanow.snn_simulator;

import romanow.snn_simulator.UniExcept;

/**
 *
 * @author romanow
 */
public class UniException extends Exception{
    public int exType;
    public String userMessage;
    public String sysMessage="";
    public Throwable ee;
    public boolean stackTrace=false;
    public UniException(int exType, String userMessage, Throwable ee, boolean stackTrace){
        this.exType = exType;
        this.userMessage = userMessage;
        this.ee = ee;
        this.stackTrace = stackTrace;
        if (ee!=null){
            sysMessage=ee.getMessage()+"\n"+ee.toString();
            if (stackTrace){
                StackTraceElement dd[]=ee.getStackTrace();
                for (StackTraceElement vv:dd) {
                    sysMessage += "\n" + vv.getClassName() + "." + vv.getMethodName() + ":" + vv.getLineNumber();
                    }
                }
            }
        }
    public UniException(int eXtype){ this(eXtype,"",null,false); }
    public UniException(int eXtype, String userMessage){
        this(eXtype,userMessage,null,false); 
        }
    public UniException(int eXtype, String userMessage, String sysMessage0){ 
        this(eXtype,userMessage,null,false); 
        sysMessage = sysMessage0;
        }
    public String toString(){
        return UniExcept.exceptTypes[exType]+":"+userMessage+"\n"+sysMessage;
        }
}
