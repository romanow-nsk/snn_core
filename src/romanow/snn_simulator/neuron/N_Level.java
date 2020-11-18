/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_NeuronStep;

/**
 *
 * @author romanow
 */
public class N_Level extends N_SingleSynapsed{
    private float spikeLevel=0.5f;
    @Override
    public void changePotecial(I_NeuronStep Back) {
        if (synapse == null){
            System.out.println("!!!!!");
            return;
            }
        boolean on = synapse.getSpike() > spikeLevel;
        setSpike(on);
        }

    public void setSpikeLevel(float spikeLevel) {
        this.spikeLevel = spikeLevel;
        }
    @Override
    public String getTypeName() {
        return "Пороговый вход";
        }    
}
