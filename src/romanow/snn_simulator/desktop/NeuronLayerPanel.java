/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JPanel;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.fft.FFTParams;

/**
 *
 * @author romanow
 */
public class NeuronLayerPanel extends JPanel{
    private int ww,hh;
    private final int stepDy=2;
    private int cStep=0;
    private int nSteps=0;
    private float maxVal=0;           // Автоматическая коррекция яркости
    private float bright=10;
    public void setBright(float bb){
        bright = bb;
        }
    private boolean white;
    
    public NeuronLayerPanel(int x0,int y0,int dx, int dy,boolean white){
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
    public void paint(float spikes[], FFTParams params){
        if (params.logFreqMode())
            paint(spikes,params.subToneCount());
        else
            paint(spikes);
        }
    public void paint(float spikes[],int subToneCount){
        Graphics gg = getGraphics();
        if (cStep==0){
            gg.setColor(white ? Color.white : Color.black);
            gg.fillRect(0, 0, ww-1, hh-1);
            }
        int dx = ww/spikes.length;
        int xx = 5;
        for(int i=0;i<spikes.length;i+=subToneCount, xx+=dx*subToneCount){
            gg.setColor(i%(12*subToneCount)==0 ? Color.red :Color.gray);
            gg.drawLine(xx, 0, xx, hh-1);                
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
            int val = (int) (bright*255*spikes[i]/GBL.FireON);
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
    public void paint(float spikes[]){          // Линейный спектр
        Graphics gg = getGraphics();
        if (cStep==0){
            gg.setColor(white ? Color.white : Color.black);
            gg.fillRect(0, 0, ww-1, hh-1);
            }
        int dd=10;
        gg.setColor(Color.red);
        for(int i=0;i<spikes.length;i+=dd){
            int xx = i/10*11;
            gg.setColor(i%100==0 ? Color.red : Color.gray);
            gg.drawLine(xx, 0, xx, hh-1);
            }
        int yy = (cStep%nSteps)*stepDy;
        gg.setColor(white ? Color.white : Color.black);
        gg.fillRect(0, yy, ww-1, stepDy);
        maxVal=0;
        for(int i=0;i<spikes.length;i++){
            if (spikes[i] > maxVal)
                maxVal = spikes[i];
        }
        for(int i=0;i<spikes.length;i++){
            int val = (int) (bright*255*spikes[i]/GBL.FireON);
            if (val <0)
                val=0;
            if (val >255)
                val=255;
            if (white)
                val = 255-val;
            gg.setColor(new Color(val,val,val));
            int xx = i/10*11+i%10+1;
            gg.fillRect(xx,yy, 1, stepDy);
            }
        gg.setColor(Color.red);
        gg.drawLine(0, yy+stepDy, ww-1, yy+stepDy);
        cStep++;
    }
}
