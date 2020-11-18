/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.model;

import romanow.snn_simulator.layer.NL_PerformGPU;
import romanow.snn_simulator.layer.NL_NeuronLayer;
import romanow.snn_simulator.neuron.N_Repeater;
import romanow.snn_simulator.neuron.N_Smooth;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_NetParams;
import romanow.snn_simulator.I_NeuronLayer;
import romanow.snn_simulator.TypeFactory;
import romanow.snn_simulator.fft.FFTParams;
import romanow.snn_simulator.layer.LayerStatistic;
import romanow.snn_simulator.layer.NL_Smooth;
import romanow.snn_simulator.neuron.N_Gavrilov;
import romanow.snn_simulator.neuron.N_Level;

public class NLM_Gavrilov extends NLM_Base{
    private NL_Smooth smooth=null;
    private I_NeuronLayer gavrilov=null;
    private LayerStatistic stats[]=new LayerStatistic[3];
    public NLM_Gavrilov(){
        super();
        }  
    @Override
    public void initModel(int subTones, I_NetParams params) throws Exception{
        super.initModel(subTones, params);
        gavrilov = new NL_NeuronLayer();
        gavrilov.createLayer(new N_Gavrilov(), size());
        smooth = new NL_Smooth(5);
        layer.createLayer(new N_Level(), size());        
        layer.addSynapse(inputs);
        gavrilov.addInputsLinear(5, layer);        
        smooth.addSynapse(gavrilov);
        smooth.setWidth(5);
        for(int i=0;i<layer.size();i++){
            ((N_Gavrilov)gavrilov.get(i)).setDH(0.05f);
            ((N_Level)layer.get(i)).setSpikeLevel(0.4f);
            }
        stats[0]=new LayerStatistic(inputs,"Вход");
        stats[1]=new LayerStatistic(smooth,"Слой 3");
        stats[2]=new LayerStatistic(gavrilov,"Слой 2");
        }
    @Override
    public void reset(FFTParams pars) throws UniException{
        smooth.reset(pars);
        gavrilov.reset(pars);
        layer.reset(pars);
        }
    @Override
    public float[] step(float[] in, I_NeuronStep back)  throws UniException{
        inputs.setValues(in);
        smooth.step(back);
        gavrilov.step(back);
        layer.step(back);
        stats[0].addStatistic();
        stats[1].addStatistic();
        stats[2].addStatistic();
        layer.synch();
        smooth.synch();
        gavrilov.synch();
        return smooth.getSpikes();
        }
    @Override
    public String getTypeName() {
        return "3 слоя";
        } 
    @Override
    public TypeFactory<LayerStatistic> getFactory() {
        return new TypeFactory<LayerStatistic>(){
            { add(stats[0]); add(stats[1]); add(stats[2]);}
            };
        }
    @Override
    public LayerStatistic getStatistic(int index){
        return stats[index];
        }

}
