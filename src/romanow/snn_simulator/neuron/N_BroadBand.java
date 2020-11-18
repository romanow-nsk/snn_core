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
//--------- Совпадение часто по широкой полосе
public class N_BroadBand extends N_SingleCatch{
    private final static float delta=0.5F;
    private final static float level0=0.50F;
    @Override
    public void changePotecial(I_NeuronStep Back) {
        setSpike(false);
        reset();
        int sz = synapses.size();
        if (sz <=1)
            return;
        float v0 = synapses.get(0).getSpike();
        if (v0<level0)
            return;
        for (int i=1;i<sz;i++){ 
            float v1 = synapses.get(i).getSpike();
            if (Math.abs(v0 - v1) > delta)
                return;
            }
        setSpike(true);
    } 
    @Override
    public String getTypeName() {
        return "Широкий";
        }        
}
