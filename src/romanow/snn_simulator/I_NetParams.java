/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import romanow.snn_simulator.neuron.N_BaseNeuron;

/**
 *
 * @author romanow
 */
public interface I_NetParams {
    public int getSynapsesCount();
    public int getNeightborsCount();
    public N_BaseNeuron getNeuronProto();       // Получить нейрон-прототип
    public boolean getLearningMode();
}
