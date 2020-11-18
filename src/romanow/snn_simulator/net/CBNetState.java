/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.net;

/**
 *
 * @author romanow
 */
public interface CBNetState {
    public void onStart();
    public void onFinish();
    public void onNeuron(MDNeuron nr);
    public void onCatch(MDNeuron nr, MDCatch nc);
}
