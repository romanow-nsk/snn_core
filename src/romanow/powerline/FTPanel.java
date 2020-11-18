/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.powerline;

import romanow.snn_simulator.desktop.*;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.GBL;

/**
 *
 * @author romanow
 */
public class FTPanel extends JPanel{
    private int ww,hh;
    private final int stepDy=2;
    private int cStep=0;
    private int nSteps=0;
    private float maxVal=0;           // Автоматическая коррекция яркости
    private boolean white;
    
    public FTPanel(int x0,int y0,int dx, int dy,boolean white){
        super();
        this.white = white;
        this.setBounds(x0, y0, dx, dy);
        ww = dx;
        hh = dy;
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        //setPreferredSize(new java.awt.Dimension(1024, 350));
        reset();
        }
    public void reset(){
        maxVal=0;
        cStep=0;
        nSteps=hh/stepDy;           // Кол-во шагов на графике
        }
    public void paint(int masX, int masY, float spikes[], float stepHZ){
        Graphics gg = getGraphics();
        if (cStep==0){
            gg.setColor(white ? Color.white : Color.black);
            gg.fillRect(0, 0, ww-1, hh-1);
            }
        int dx = ww/spikes.length;
        if (dx==0)
            dx=1;
        int xx = 5;
        //for(int i=0;i<spikes.length;i+=subToneCount, xx+=dx*subToneCount){
        //    gg.setColor(i%(12*subToneCount)==0 ? Color.red :Color.gray);
        //    gg.drawLine(xx, 0, xx, hh-1);                
        //    }
        if (stepHZ!=0){
            for(int i=0;i<50;i++){
                int zz = (int)(i*dx/stepHZ);
                gg.setColor(i%10==0 ? Color.red : Color.gray);
                if (zz>=ww) break;
                gg.drawLine(zz, hh-1, zz, 0);                    
                }                               
            }
        xx=6;
        int yy = (cStep%nSteps)*stepDy;
        gg.setColor(white ? Color.white : Color.black);                
        gg.fillRect(0, yy, ww-1, stepDy);
        maxVal=0;
        for(int i=0;i<spikes.length;i++){
            if (spikes[i] > maxVal)
                maxVal = spikes[i];
            }
        for(int i=0;i<spikes.length;i++,xx+=dx){
            int val = (int) (masX*255*spikes[i]/GBL.FireON);
            if (val <0)
                val=0;
            if (val >255)
                val=255;
            if (white)
                val = 255-val;
            gg.setColor(new Color(val,val,val));
            gg.fillRect(xx,yy, dx, stepDy);                
            }
        gg.setColor(Color.red);
        gg.drawLine(0, yy+stepDy, ww-1, yy+stepDy);                
        cStep++;
        }
}
