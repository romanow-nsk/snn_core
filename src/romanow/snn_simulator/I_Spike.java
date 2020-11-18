/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

/**
 *
 * @author romanow
 */
public interface I_Spike {      // источник спайков
    public float getSpike();    // 
    public boolean hasValue();  //
    public boolean getFire();   // Возвращает логическое значение спайка
}
