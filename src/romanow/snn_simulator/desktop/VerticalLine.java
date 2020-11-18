/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JPanel;

/**
 *
 * @author romanow
 */
public class VerticalLine extends NoImage{
    private static int imageId=2;
    @Override
    public int getPixel(int x, int y, int nsz) {
        return getPixel(x,y,nsz,0,0);
        }
    @Override
    public int getPixel(int x, int y, int nsz, int shiftX, int shiftY) {
        if (x - shiftX !=nsz/2)
            return 0;
        return 255;
        }
    @Override
    public void paint(JPanel Img) {
        Rectangle rr = Img.getBounds();
        rr.height = rr.width;     // Выровнять пропорции
        Img.setBounds(rr);        
        Graphics gg = Img.getGraphics();
        gg.setColor(Color.black);
        gg.fillRect(0, 0, Img.getWidth()-1, Img.getHeight()-1);
        gg.setColor(Color.white);
        gg.drawLine(Img.getWidth()/2, 0, Img.getWidth()/2, Img.getHeight()-1);
        }
    @Override
    public int getTeachImageId() {
        return imageId;
        }
    @Override
    public String getTeachImageName() {
        return "Вертикальная";
        }
}
