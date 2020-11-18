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
public class N_Stable extends N_SingleCatch{
    private final static int delta=10;
    private final static int level0=50;
    private final static int diff=10;
    private float prev[] = null;
    public N_Stable(){
        }
    @Override
    public void changePotecial(I_NeuronStep Back){
        int n = synapses.size();
        if (prev==null){
            prev = new float[n];
            for(int i=0;i<n;i++)
                prev[i]=synapses.get(i).getSpike();
            setSpike(false);
            return;
            }
        float sum=0, sum2=0;
        for (int i=0;i<n;i++){ 
            float cur = synapses.get(i).getSpike();
            float diff = prev[i]-cur;
            prev[i]=cur;
            sum2+=diff*diff;
            }
        setSpike(Math.sqrt(sum2/n)>level0);
    }     
    @Override
    public String getTypeName() {
        return "Стабильный";
        }    
}
