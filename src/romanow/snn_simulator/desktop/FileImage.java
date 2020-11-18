/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import java.awt.FileDialog;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class FileImage implements IImage{
    private static int imageId=1;
    private BufferedImage img=null;
    public BufferedImage getImg(){ return img; }
    public FileImage(BufferedImage img0){
        img = img0;
        }
    public int getPixel(int x, int y, int nsz){
        return getPixel(x,y,nsz,0,0);
        }
    public int getPixel(int x, int y, int nsz, int shiftX, int shiftY){
        float dx = img.getWidth()/nsz;
        float dy = img.getHeight()/nsz;
        int xx = (int)((x+shiftX)*dx);
        int yy = (int)((y+shiftY)*dy);
        int val = 0;
        if (xx>=0 && xx<img.getWidth() && yy>=0 && yy<img.getHeight()){
            val = img.getRGB(xx,yy);
            val = ((val & 0x0FF) + ((val>>8) & 0x0FF) + ((val>>16) & 0x0FF))/3;
            }
        return val;
        }
    public void paint(JPanel Img){
        int yy = img.getHeight();
        int xx = img.getWidth();
        Rectangle rr = Img.getBounds();
        rr.height = rr.width * yy / xx;     // Выровнять пропорции
        Img.setBounds(rr);
        Img.getGraphics().drawImage(img, 0, 0, Img.getWidth(), Img.getHeight(), null);
        }
    @Override
    public int getTeachImageId() {
        return imageId;
        }
    @Override
    public String getTeachImageName() {
        return "Картинка";
    }
    @Override
    public void loadImage(JFrame frame) throws IOException{
        FileDialog dlg0=new FileDialog(frame,"Выберите картинку",FileDialog.LOAD);
        dlg0.setFile("a.dat");
        dlg0.show();
        String ss=dlg0.getDirectory()+dlg0.getFile();
            InputStream in = new FileInputStream(ss);
            img = ImageIO.read(in);
            in.close();
        }    
}
