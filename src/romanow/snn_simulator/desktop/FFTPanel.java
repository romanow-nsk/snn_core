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

/**
 *
 * @author romanow
 */
public class FFTPanel extends JPanel{
    private Color back;
    private Color fore;
    private int ww,hh;
    public FFTPanel(int x0,int y0,int dx, int dy,boolean white){
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
    public void paint(int masX, int masY, float vals[], boolean positive, int subToneCount){
        if (vals==null || vals.length==0)
            return;
        int y0 = positive ? hh-5 : hh/2; 
        int dx = (int)(masX/10*1024./vals.length);
        if (dx==0)
            dx=1;
        Graphics gg = getGraphics();
        gg.setColor(back);
        gg.fillRect(0, 0, ww-1, hh-1);
        if (subToneCount!=0){
            for(int i=0;i<FFT.Octaves*12;i++){
                gg.setColor(i%12==0 ? Color.red : Color.gray);
                int pp = i*dx*subToneCount;
                if (pp>=ww) break;
                gg.drawLine(pp, hh-1, pp, 0);                    
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
    public void paint(int masX, int masY, FFT fft){
        clear();
        Graphics gg = getGraphics();
        if (!fft.isLogFreqMode()){
            float vals[] = fft.getSpectrum();
            int gridIdx[] = fft.getToneIndexes();
            int N = (vals.length/FFT.Size0);
            int subToneCount = fft.getSubToneCount();
            for(int i=0; i<gridIdx.length;i += subToneCount){
                int bb = 2*gridIdx[i]*masX/10/N;
                if (bb>=ww) break;
                gg.setColor(i%(12*subToneCount)==0 ? Color.red : Color.gray);
                gg.drawLine(bb, hh-1, bb, 0);                
                }
            gg.setColor(Color.red);
            int bb1= 2*gridIdx[0]*masX/10/N;
            gg.drawLine(bb1, hh-1, bb1, 0);                
            paintValues(masX,masY,vals,N,2);
            }
        else{
            float vals[] = fft.getLogSpectrum();
            int octSize = fft.getSubToneCount();
            for(int i=0;i<FFT.Octaves*12;i++){
                gg.setColor(i%12==0 ? Color.red : Color.gray);
                int pp = 2*i*octSize*masX/10;
                if (pp>=ww) break;
                gg.drawLine(pp, hh-1, pp, 0);                    
                }                   
            paintValues(masX,masY,vals,1,1);
            }
        }
}
