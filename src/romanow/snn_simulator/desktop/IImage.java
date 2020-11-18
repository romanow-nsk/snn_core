/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author romanow
 */
public interface IImage {
    public int getPixel(int x, int y, int nsz);
    public int getPixel(int x, int y, int nsz, int shiftX, int shiftY);
    public void paint(JPanel Img);
    public int getTeachImageId();
    public String getTeachImageName();
    public void loadImage(JFrame frame) throws IOException;
}
