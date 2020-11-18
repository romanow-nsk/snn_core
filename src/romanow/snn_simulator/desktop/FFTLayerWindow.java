/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import java.awt.Graphics;
import javax.swing.JButton;
import javax.swing.JTextField;
import romanow.snn_simulator.fft.FFT;

/**
 *
 * @author romanow
 */
public class FFTLayerWindow extends LayerWindow{
    private FFTPanel panel=null;
    private javax.swing.JSlider Freq;
    private javax.swing.JSlider Ampl;
    private boolean positive=false;
    private int subToneCount=0;
    private int masY=50;
    private int masX=10;
    JTextField VAmpl;
    private float last[]=null;

    public FFTLayerWindow(int num,LayerWindowCallBack back,  int hight, String title,boolean white) {
        this(num,back,hight,title,false,0,white);
        }
    public FFTLayerWindow(int num,LayerWindowCallBack back,  int hight, 
        String title, boolean positive,int subToneCount,boolean white) {
        super(num,title,hight,50,30,white);
        this.positive = positive;
        this.subToneCount = subToneCount;
        panel = new FFTPanel(13,10,1050,hight-45,white);
        add(panel);
        setCallBack(back);
        Ampl = new javax.swing.JSlider();
        Ampl.setMinimum(1);
        Ampl.setMaximum(1000);
        Ampl.setValue(250);
        Ampl.setOrientation(javax.swing.JSlider.VERTICAL);
        Ampl.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                masY=Ampl.getValue();
                VAmpl.setText(""+masY);
                if (last!=null)             // Перерисовать в новом масштабе
                    paint(last, null);
                }
            });
        Ampl.setBounds(1060, hight-180, 30, 110);
        add(Ampl);
        VAmpl =new JTextField();
        VAmpl.setBounds(1070, hight-220, 40, 30);
        add(VAmpl);
        Freq = new javax.swing.JSlider();
        Freq.setMinimum(1);
        Freq.setMaximum(100);
        Freq.setValue(10);  
        Freq.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                masX=Freq.getValue();
                if (last!=null)             // Перерисовать в новом масштабе
                    paint(last, null);
                }
            });
        Freq.setBounds(920, hight-30, 120, 23);
        add(Freq);
        }
    @Override
    public void paint(FFT fft) {
        panel.paint(masX, masY, fft);
        }
    @Override
    public void paint(float[] vals, String title) {
        last = vals;
        panel.paint(masX, masY, vals,positive,subToneCount);
        }

    @Override
    public void paint(Graphics g) {
        super.paint(g); 
        if (last!=null)
            panel.paint(masX, masY, last, positive,subToneCount);
        }
    public void paint(float[] vals) {
        last = vals;
        panel.paint(masX, masY, vals,positive,subToneCount);
        }  
}
