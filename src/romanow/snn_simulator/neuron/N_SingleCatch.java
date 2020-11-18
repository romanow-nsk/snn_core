/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_Spike;
import romanow.snn_simulator.Token;
import romanow.snn_simulator.layer.NL_Catch;


public class N_SingleCatch extends N_BaseNeuron {
    NL_Catch synapses = new NL_Catch();
    private int inputIndexes[]=null;
    @Override
    public void addInputsLinear(int idx, int size, I_Layer src) throws UniException{
        synapses.addInputsLinear(idx, size, src);
        }    
    @Override
    public void addInputsOctave(int idx, int step, int size, I_Layer src) throws UniException{
        synapses.addInputsOctave(idx, step, size, src);
        }    
    @Override
    public void addSynapse(I_NeuronOutput in, float w) {
        synapses.addSynapse(in,w); 
        }
    public void addPotentialForAll(){
        int sz = synapses.size();
        for(int i=0;i<sz;i++){
            addPotential(synapses.get(i).getSpike());
            //System.out.println("* "+getPotential());
            }
        }
    public I_NeuronOutput get(int idx){
        return synapses.get(idx);
        } 
    public int size(){
        return synapses.size();
        }
    @Override
    public boolean hasValue() {
        return false;
        }
    @Override
    public boolean getFire() {
        return false;
        }
    @Override
    public void changePotecial(I_NeuronStep Back) {
        }
    @Override
    public String getTypeName() {
        return "Многовходовой";
        }  
    @Override
    public String getName() {
        return getTypeName();
        }    
    @Override
    public String save() throws IOException {
        String ss =  super.save()+"/"+synapses.size(); 
        for(int i=0; i<synapses.size();i++)
            ss+="/"+synapses.get(i).getUID();
        return ss;
        }
    @Override
    public void load(Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        super.load(in,map); 
        int cnt = in.getInt();
        inputIndexes = new int[cnt];
        for(int i=0;i<cnt;i++){
            inputIndexes[i] = in.getInt();
            }
        }
    @Override
    public String getFormatLabel() {
        return super.getFormatLabel()+"/size{Номер входа}";
        }

    @Override
    public void createLinks(HashMap<Integer, I_NeuronOutput> map) {
        for (int i=0;i<inputIndexes.length;i++){
            I_NeuronOutput sp = map.get(inputIndexes[i]);
            if (sp==null)
                System.out.println(getUID()+" "+inputIndexes[i]);
            else
                synapses.addSynapse(sp,1);      // БЕЗ ВЕСА
            }
    }
    
 }
