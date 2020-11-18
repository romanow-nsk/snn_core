/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.UniExcept;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_Spike;

/**
 *
 * @author romanow
 */
public class N_SingleSynapsed extends N_BaseNeuron{
    I_NeuronOutput synapse=null;
    float weight = 1f;
    private int inputIndex=0;
    public void addInputs(int idx, int size, I_Layer src) throws UniException{
        if (size!=1)
            UniExcept.calcEx("Синапсов больше 1");
        synapse = src.get(idx);
        }
    @Override
    public void addSynapse(I_NeuronOutput in, float weight) {
        synapse = in; 
        this.weight = weight;
        } 
    @Override
    public boolean hasValue() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return false;
        }
    @Override
    public boolean getFire() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return false;
        }
    @Override
    public void addInputsLinear(int idx, int size, I_Layer src) throws UniException {
        //if (size!=1)
        //    throw new UnsupportedOperationException("Не поддерживается: только 1 синапс"); //To change body of generated methods, choose Tools | Templates.
        synapse = src.get(idx);
        weight = 1f;
        }
    @Override
    public void addInputsOctave(int idx, int octaveStep, int size, I_Layer src) throws UniException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }
    @Override
    public void changePotecial(I_NeuronStep Back) {
        }
    @Override
    public String getTypeName() {
        return "Одновходовой(-)";
        }   
    @Override
    public String getName() {
        return getTypeName();
        }
    @Override
    public String save() throws IOException {
        return super.save()+"/"+synapse.getUID()+"/"+weight; 
        }

    @Override
    public void load(romanow.snn_simulator.Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        super.load(in,map); 
        inputIndex = in.getInt();
        weight = in.getFloat();
    }

    @Override
    public String getFormatLabel() {
        return super.getFormatLabel()+"/<Номер входа>/вес";
        }

    @Override
    public void createLinks(HashMap<Integer, I_NeuronOutput> map) {
        synapse = map.get(inputIndex);
        if (synapse==null)
            System.out.println(getUID()+" "+inputIndex);

    }
    
}
