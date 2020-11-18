/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import romanow.snn_simulator.UniExcept;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_RegularConnector;
import romanow.snn_simulator.Token;

/**
 *
 * @author romanow
 */
public class NL_Catch extends Vector<I_NeuronOutput> implements I_Layer, I_RegularConnector{
    @Override
    public float[] getSpikes(){
        float out[] = new float[size()];
        for(int i=0;i<size();i++)
            out[i] = get(i).getSpike();
        return out;
        }
    @Override
    public void addInputsLinear(int idx, int size, I_Layer src) throws UniException{
        int cnt = GBL.addInputsLinear(this, idx, size, src);
        }
    @Override
    public void addInputsOctave(int idx, int octaveStep, int size, I_Layer src) throws UniException {
        int cnt = GBL.addInputsOctave(this, idx, octaveStep, size, src);
        } 
    public void addSynapse(I_NeuronOutput in, float w){
        add(in);
        }
    @Override
    public int getSubToneCount() {
        UniExcept.calcEx("Ловушка не поддерживает getSubToneCount()");
        return 0;
        }
}
