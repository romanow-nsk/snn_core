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
public interface I_NeuronOutput extends I_Spike{
    public int getUID();
    public void setUID(int uid);
}
