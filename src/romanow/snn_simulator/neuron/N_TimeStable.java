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
public class N_TimeStable extends N_SingleSynapsed{
    private boolean excitated=false;       // Состояние - возбужден
    private float prev=0;
    private int upDownCount=0;
    private int upDownCount0=5;
    private float delta=0.05F;
    private int count=0;
    private float level0 = 0.50F;

    @Override
    public void reset() {
        super.reset(); 
        excitated = false;
        count=0;
        }
    @Override
    public void changePotecial(I_NeuronStep Back) {
        float vv = prev;              // vv - предыдущее
        prev = synapse.getSpike();  // prev - текущее
        if (prev < level0){
            reset();
            setSpike(false);
            return;
            }
        boolean up = prev - vv > delta;
        boolean down = vv - prev > delta;
        if (!up && !down){
            upDownCount=0;
            setSpike(excitated);
            return;
            }
        if (up){
            if (!excitated){
                upDownCount++;
                if (upDownCount == upDownCount0){
                    excitated = true;
                    upDownCount=0;
                    }
                }
            }
        if (down){
            if (excitated){
                upDownCount++;
                if (upDownCount == upDownCount0){
                    excitated = false;
                    upDownCount=0;
                    }
                }
            }
        setSpike(excitated);
        }
    @Override
    public String getTypeName() {
        return "Пост.уровень";
        }        
}
