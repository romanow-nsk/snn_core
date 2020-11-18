package romanow.snn_simulator;

import romanow.snn_simulator.neuron.N_BaseNeuron;

/**
 *
 * @author romanow
 */
public interface I_NeuronStep {
    public void onFire(N_BaseNeuron nw);  
    public void onMessage(int level,String mes);
}
