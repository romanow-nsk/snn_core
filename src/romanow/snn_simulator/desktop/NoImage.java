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
public class NoImage implements IImage{
    @Override
    public int getPixel(int x, int y, int nsz) {
        return 0;
        }
    @Override
    public int getPixel(int x, int y, int nsz, int shiftX, int shiftY) {
        return 0;
        }
    @Override
    public void paint(JPanel Img) {
        }
    @Override
    public int getTeachImageId() {
        return 0;
        }
    @Override
    public String getTeachImageName() {
        return "...";
        }    
    @Override
    public void loadImage(JFrame frame) throws IOException {
        }
}
