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
public class Layer extends Name{
    public String type="";
    public Layer(String name, String type){
        super(name);
        this.type = type;
        }
    public Vector<Param> params = new Vector();
    public void add(Param pp){
        params.add(pp);
        }    
}
