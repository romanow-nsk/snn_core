/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import romanow.snn_simulator.UniException;

/**
 *
 * @author romanow
 */
public interface I_Layer {
    public int getSubToneCount();
    public int size();
    public I_NeuronOutput get(int idx) throws UniException;
    public float []getSpikes() throws UniException;
}
