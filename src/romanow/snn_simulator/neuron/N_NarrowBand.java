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
public class N_NarrowBand extends N_SingleCatch{
    private final static float level0=0.5F;    
    public N_NarrowBand(){
        setLevel(level0);
        }
    @Override
    public void changePotecial(I_NeuronStep Back) {
        int sz = synapses.size();
        if (sz <=1){
            setSpike(false);
            return;
            }
        int mid = sz/2;
        for (int i=1;i<sz;i++){ 
            if (i==mid)
                addPotential(synapses.get(i).getSpike());
            else
                addPotential(-synapses.get(i).getSpike());
            }
    }    
    @Override
    public String getTypeName() {
        return "Широкополосный";
        }    
}
