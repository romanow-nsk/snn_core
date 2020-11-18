/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

import romanow.snn_simulator.I_NeuronStep;

/**
 *
 * @author romanow
 */
public class N_Repeater extends N_SingleSynapsed{
    @Override
    public void changePotecial(I_NeuronStep Back) {  // Переопределяется в ПК
        float in = synapse.getSpike();
        setOutValue(in);
        }
    @Override
    public String getTypeName() {
        return "Повторитель";
        }    
}
