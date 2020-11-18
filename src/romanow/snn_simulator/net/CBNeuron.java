/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.net;

// CallBAck нейрона
public interface CBNeuron {
    public void onAxonSpike(MDNeuron nr);
    public void onPaceMakerSpike(MDNeuron nr);
    public void onDendritSpike(MDNeuron nr, MDCatch nc);
    public void onStateRelax(MDNeuron nr);
    public void onStateActive(MDNeuron nr);
    public void onEvent(MDNeuron nr, MDCatch ch, String evt);
    public int  getInputActivity(MDNeuron nr);  // Уровень входного сигнала нейрона
    public int  getTeachImageId();              // Id обучающего образа (0 - отсутствует)    
}
