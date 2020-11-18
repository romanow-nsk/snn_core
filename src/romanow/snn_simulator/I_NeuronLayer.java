/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import java.util.HashMap;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.fft.FFTParams;

/**
 *
 * @author romanow
 */
public interface I_NeuronLayer extends I_Layer,I_TypeName,I_ObjectName,I_TextStream{
    public void createLayer(I_Neuron proto, int size) throws Exception;
    public void reset(FFTParams pars) throws UniException;
    public float[] getSpikes() throws UniException;
    public float getSpike(int idx) throws UniException;
    public void step(I_NeuronStep back) throws UniException;
    public void synch();
    public void addSynapse(I_Layer src) throws UniException;
    public void addSynapseShifted(int offset,I_Layer src) throws UniException;
    public void addInputsLinear(int size,I_Layer src) throws UniException;
    public int numerateUID(int uid);
    public I_Neuron findByUID(int uid);
    public void setLearningMode(boolean learinig);    
}
