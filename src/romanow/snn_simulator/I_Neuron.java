/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import java.util.HashMap;

/**
 *
 * @author romanow
 */
public interface I_Neuron extends I_NeuronOutput,I_TypeName,I_TextStream{
    public void changePotecial(I_NeuronStep back);
    public void setOutValue(float val);
    public void setSpike(boolean val);
    public void addSynapse(I_NeuronOutput in, float weight);
    public void reset();
    public void onFire(I_NeuronStep back);
    public void changeLevel(float delta);
    public float getLevel();
    public void createLinks(HashMap<Integer, I_NeuronOutput> map);
    public void setLearningMode(boolean learinig);
}
