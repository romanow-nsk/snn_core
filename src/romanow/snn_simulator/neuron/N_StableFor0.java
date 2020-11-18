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
//--------- Совпадение по синапсу 0, остальные суммируются линейно
public class N_StableFor0 extends N_SingleCatch{
    private final static float K=0.1F;
    private final static int level0=50;
    private final static int diff=10;
    private final static int memDepth=5;
    private int iNext=0;              // Текущая индекс запоминанания
    private float sum=0;
    private float sum2=0;
    private float queue[] = new float[memDepth];
    @Override
    public void reset(){
        super.reset();
        sum=0;
        sum2=0;
        iNext=0;
        for(int i=0;i<memDepth;i++)
            queue[i]=0;
        setLevel(500);
        }
    public N_StableFor0(){
        reset();
        }
    @Override
    public void changePotecial(I_NeuronStep Back){
        addPotential(get(1).getSpike());    
        addPotential(get(2).getSpike());    
        for(int i=2;i<size();i++)               // Остальные - тормозящие
            addPotential(-get(i).getSpike());    
        float vv = get(0).getSpike();
        queue[iNext]=vv;
        sum=0;
        sum2=0;
        for(int i=1;i<memDepth;i++){
            vv=queue[i]-queue[i-1];
            vv = vv*vv;
            sum+=vv;
            }
        //System.out.println(sum);
        addPotential(sum*K);
        iNext++;
        if (iNext == memDepth)
            iNext=0;
        } 
    @Override
    public String getTypeName() {
        return "Стабильный-0";
        }        
}
