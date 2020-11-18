/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.lang;

import java.util.Vector;

/**
 *
 * @author romanow
 */
public class Statistic extends Name{
    public String layer="";
    public Statistic(){ super(""); }
    public Statistic(String name, String layer){
        super(name);
        this.layer = layer;
        }
}
