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
public interface CBNet{
    //--------- События --------------------------------------------------------
    public void onStart();
    public void onFinish();
    public void onStep(int nstep);
    public void onFatal(int nstep, Throwable ee);
    public void onAxonSpike(int nstep, MDNeuron nr);
    public void onPaceMakerSpike(int nstep, MDNeuron nr);
    public void onDendritSpike(int nstep, MDNeuron nr, MDCatch nc);
    public void onStateRelax(int nstep, MDNeuron nr);
    public void onStateActive(int nstep, MDNeuron nr);
    public void onEvent(MDNeuron nr, MDCatch ch, String evt);    
    //--------- Запросы --------------------------------------------------------
    public boolean testStop();                  // Внешнее условие завершения
    public void delay();                        // Внешняя задержка (для визуализации)
    public int  getInputActivity(MDNeuron nr);  // Уровень входного сигнала нейрона
    public int  getTeachImageId();              // Id обучающего образа (0 - отсутствует)
}
