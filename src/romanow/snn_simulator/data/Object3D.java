/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.data;

/**
 *
 * @author romanow
 */
public interface Object3D extends Object2D{
    public int getZ();
    public void setZ(int z);
    public float diff(Object3D two);    
}
