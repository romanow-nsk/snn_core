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
//  Отслеживает ПЛАТО сигнала
public class N_Contrast extends N_SingleSynapsed{
    private float K=3;       
    private int level=50;

    /*
    @Override
    public void calcSpike() {
        int vv = (int)(K*synapse.getSpike());
        if (vv < DDN.FireOFF)
            vv=DDN.FireOFF;
        if (vv > DDN.FireON)
            vv=DDN.FireON;
        nextSpikeVal = vv;
        } 
    */
    @Override
    public void changePotecial(I_NeuronStep Back){
        setSpike(synapse.getSpike() > level);
        }
    @Override
    public String getTypeName() {
        return "Контрастирующий";
        }        
}
