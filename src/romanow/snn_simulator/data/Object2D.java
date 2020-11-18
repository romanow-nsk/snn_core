/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.data;

import romanow.snn_simulator.I_BinaryStream;

/**
 *
 * @author romanow
 */
public interface Object2D extends I_BinaryStream{
    public int getX();
    public int getY();
    public void setX(int x);
    public void setY(int y);
    public float diff(Object2D two);
}
