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
public class N_Classic extends N_SingleCatch{
    public N_Classic(){
        setLevel(1);
        }
    @Override
    public void changePotecial(I_NeuronStep Back) {
        int sz = synapses.size();
        for (int i=0;i<sz;i++){ 
            addPotential(synapses.get(i).getSpike()/sz);
            }
    }    
    @Override
    public String getTypeName() {
        return "Классический";
        }    
}
