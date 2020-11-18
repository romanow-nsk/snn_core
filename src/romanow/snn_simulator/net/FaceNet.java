/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.net;

import romanow.snn_simulator.data.VectorX;


public interface FaceNet{
    public void createNetParams(int size, int near);// Вычислить параметры
    public MDNeuron createNeuron(MDNet parent);     // Создать нейрон
                                                    // Создать диапазон контактов
    public VectorX<MDNeuron> createNeuronLinks(int idx);
    public void prepareNetBefore();
    public void prepareNetAfter();
    }
