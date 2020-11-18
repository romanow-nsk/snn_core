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
// Среднее - дисперсия
public class N_BroadBand2 extends N_SingleCatch{
    private final static float delta=0.10F;
    private final static float level0=0.5F;
    private final static float diff=0.10F;
    public N_BroadBand2(){
        setLevel(30);
        }
    @Override
    public void changePotecial(I_NeuronStep Back){
        setSpike(false);
        float sum=0, sum2=0;
        int n = synapses.size();
        for (int i=0;i<n;i++){ 
            float vv = synapses.get(i).getSpike();
            sum+=vv;
            sum2+=vv*vv;
            }
        sum/=n;                                     // Среднее
        sum2 = (float)Math.sqrt(sum2/n - sum*sum);  // Дисперсия
        if (sum<level0) 
            return;
        if (sum/sum2 < diff)                        // Среднее большое и ОДИНАКОВОЕ
            return;
        //potential+= sum;
        //return false; 
        setSpike(true);
    } 
    @Override
    public String getTypeName() {
        return "Широкий 2";
        }        
}
