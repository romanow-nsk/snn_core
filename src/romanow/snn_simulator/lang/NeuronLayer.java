/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.lang;

import com.thoughtworks.xstream.XStream;
import java.util.Vector;
import romanow.snn_simulator.neuron.N_Gavrilov;

/**
 *
 * @author romanow
 */
public class NeuronLayer extends Layer{
    public NeuronLayer(String name, String type) {
        super(name, type);
        }
    public static void main(String argv[]) throws Exception{
        XMLParser xx = new XMLParser();
        NeuronLayer nl = new NeuronLayer("aa","bb");
        nl.add(new Param("cc","12"));
        nl.add(new Param("dd","ertyu"));
        System.out.println(xx.toXML(nl));
        }    
}
