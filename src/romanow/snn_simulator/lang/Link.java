/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.lang;

/**
 *
 * @author romanow
 */
public class Link{
    public String from="";
    public String to="";
    public int type=0;
    public int param=0;
    public Link(String from, String to, int type, int param){
        this.from = from;
        this.to = to;
        this.type = type;
        this.param = param;
        }
}
