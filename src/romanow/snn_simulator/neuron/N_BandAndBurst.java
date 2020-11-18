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
//--------- Возбужение по ИЛИ c инерцией (бинарный вход)
//          Усиление при одновременности
public class N_BandAndBurst extends N_SingleCatch{
    private final static int delta=10;
    private final static int fireCount0=5;
    private int fireCount=0;
    private boolean pause = true;
    @Override
    public void changePotecial(I_NeuronStep Back) {
        reset();
        int sz = synapses.size();
        int cnt=0;
        for (int i=0;i<sz;i++){ 
            if (synapses.get(i).getSpike()!=0){
                fireCount++;
                cnt++;
                }
            }
        if (cnt >= sz - 3)
            fireCount += sz*3;
        if (fireCount !=0){
            fireCount--;
            if (!pause){
                setSpike(true);
                return;
                }
            pause = false;
            setSpike(false);
            return;
            }
        pause = true;
        setSpike(false);
        }   
    @Override
    public String getTypeName() {
        return "Burst по полосе";
        }    
}
