/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

//--- Генератор случайных спайков

import romanow.snn_simulator.I_NeuronStep;

public class N_Random extends N_SingleSynapsed{
    private float lv=0.3f;
    @Override
    public void changePotecial(I_NeuronStep Back) {  // Переопределяется в ПК
        setSpike(Math.random()<lv);
        }
    @Override
    public String getTypeName() {
        return "Случайный спайк";
        }    
}
