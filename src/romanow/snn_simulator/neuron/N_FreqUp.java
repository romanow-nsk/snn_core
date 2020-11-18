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
public class N_FreqUp extends N_SingleCatch{
    private int count=0;
    private float prevSpike=0;
    private final static int cnt0=5;

    @Override
    public void changePotecial(I_NeuronStep Back) {
        float vv = get(0).getSpike();
        if (vv <= prevSpike){
            count=0;
            prevSpike = vv;
            }
        else{
            count++;
            prevSpike = vv;
            if (count==cnt0){
                setSpike(true);
                return;
                }
            }
        setSpike(false);        
        }
    @Override
    public String getTypeName() {
        return "Возрастание входа";
        }    
}
