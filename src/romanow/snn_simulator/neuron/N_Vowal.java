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
// Нейрон для обнаружения гласных
public class N_Vowal extends N_SingleCatch{
    private final static int F0=0;  // основная
    private final static int F1=1;  // октава
    private final static int F11=2; // октава + квинта
    private final static int F12=3; // октава + б. терция
    private final static int F13=4; // октава + м. терция
    private final static int F2=5;  // две октавы
    private final static int F21=6; // две октавы + квинта
    private final static int F22=7; // две октавы + б. терция
    private final static int F23=8; // две октавы + м. терция
    private final static int F3=9;  // три октавы
    private int exhibit[][]={
    //    F0F1F11..............
        { 0,0,1,1,0,0,1,0,0,0,0,0 },    //ы
        { 0,0,1,1,1,1,1,1,1,1,1,1 },    //а
        { 0,0,1,1,0,0,0,0,0,0,0,0 },    //и
        { 0,0,1,1,1,0,1,0,0,0,0,0 },    //у
        { 0,0,1,1,1,0,1,0,0,0,1,1 },    //э
        { 0,0,1,1,1,1,0,1,0,0,0,0 },    //о ???
        };
    private int ww[]={10,12,20,17,14,12,10,8,6,4,3,2};
    private float dW=0.75F;
    private float W0=1;
    private int spikes[]=new int[12];
    private final static int vowalId=1;
    private final static float level=2;
    private final static float level0=50;
    private float sum=0;
    private int idx=0;
    public N_Vowal(){
        setLevel(100);
        setL(100);
        }
    @Override
    public void changePotecial(I_NeuronStep Back) {
        sum=0;
        int imax=0;
        float f0 = get(2).getSpike();
        for(idx=0;idx<exhibit[vowalId].length;idx++){
            float vv = ww[idx]*get(idx).getSpike();
            spikes[idx]=(int)vv;
            if (spikes[idx]> spikes[imax])
                imax=idx;
            if (exhibit[vowalId][idx]==1)
                sum+=vv/f0;
            if (exhibit[vowalId][idx]==0)
                sum-=vv/f0;
            }
        if (imax!=2)
            setSpike(false);
        else
            setSpike(sum>50);
        }
    @Override
    public void onFire(I_NeuronStep back){
        //for(int i=0;i<spikes.length;i++)
        //    System.out.print(" "+spikes[i]);
        //System.out.println(" sum="+sum);
        }
    @Override
    public String getTypeName() {
        return "Гласные";
        }    
}
