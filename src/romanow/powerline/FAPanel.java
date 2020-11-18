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

/**
 *
 * @author romanow
 */
public class FAPanel extends JPanel{
    private Color back;
    private Color fore;
    private int ww,hh;
    public FAPanel(int x0,int y0,int dx, int dy,boolean white){
        super();
        if (white){
            back = Color.white;
            fore = Color.black;
            }
        else{
            fore = Color.white;
            back = Color.black;
            }
        this.setBounds(x0, y0, dx, dy);
        ww = dx;
        hh = dy;
        setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        //setPreferredSize(new java.awt.Dimension(1024, 350));
        }
    private void clear(){
        Graphics gg = getGraphics();
        gg.setColor(back);
        gg.fillRect(0, 0, ww-1, hh-1);
        }
    private void paintValues(int masX, int masY, float vals[],int N,int half){
        Graphics gg = getGraphics();
        int dx = (int)(2*masX/10/N);
        if (dx==0)
            dx=1;
        gg.setColor(fore);
        for(int i=0;i<vals.length/half; i++){
            int bb = i*dx;
            if (bb>=ww) break;
            int ll = (int)(vals[i]*masY);
            gg.fillRect(bb, hh-1-ll, dx, ll);
            }
        }
    //---------------- Без сетки, просто все -----------------------------------
    public void paint(int masX, int masY, float vals[]){
        paint(masX,masY,vals,false,0);
        }
    public void paint(int masX, int masY, float vals[], boolean positive, float stepHZ){
        if (vals==null || vals.length==0)
            return;
        int y0 = positive ? hh-5 : hh/2; 
        int dx = (int)(masX/10*ww/vals.length);
        if (dx==0)
            dx=1;
        Graphics gg = getGraphics();
        gg.setColor(back);
        gg.fillRect(0, 0, ww-1, hh-1);
        if (stepHZ!=0){
            for(int i=0;i<50;i++){
                int xx = (int)(i*dx/stepHZ);
                gg.setColor(i%10==0 ? Color.red : Color.gray);
                if (xx>=ww) break;
                gg.drawLine(xx, hh-1, xx, 0);                    
                }                               
            }
        gg.setColor(fore);
        for(int i=0;i<vals.length; i++){
            int bb = i*dx;
            if (bb>=ww) break;
            int vv = (int)(vals[i]*masY);
            if (vals[i]<0)
                gg.fillRect(bb, y0, dx, -vv);
            else
                gg.fillRect(bb, y0-vv,dx,vv);
            }
        }
}
