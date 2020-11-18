/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import java.util.Vector;
import romanow.snn_simulator.UniException;

/**
 *
 * @author romanow
 */
public interface I_RegularConnector {
    public void addInputsLinear(int idx, int size, I_Layer src) throws UniException;    
    public void addInputsOctave(int idx, int octaveStep, int size, I_Layer src) throws UniException;    
}
