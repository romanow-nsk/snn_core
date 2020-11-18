/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

import romanow.snn_simulator.TypeFactory;
import romanow.snn_simulator.layer.NL_Integrate;

/**
 *
 * @author romanow
 */
public class NeuronFactory extends TypeFactory<N_BaseNeuron>{
    public NeuronFactory(){
        generate("romanow.snn_simulator.neuron",N_BaseNeuron.class);        
        }
}
