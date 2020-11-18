/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import javax.swing.JSlider;

/**
 *
 * @author romanow
 */
public class NeuronLayerWindow extends LayerWindow {
    private NeuronLayerPanel panel=null;
    private JSlider bright=null;
    public NeuronLayerWindow(int num,LayerWindowCallBack back, int hight, String title,boolean white) {
        super(num,title,hight,30,0,white);
        panel = new NeuronLayerPanel(13,10,1030, hight-60,white);
        add(panel);
        setCallBack(back);
        bright = new javax.swing.JSlider();
        bright.setMinimum(1);
        bright.setMaximum(2500);
        bright.setValue(250);
        bright.setOrientation(javax.swing.JSlider.VERTICAL);
        bright.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                panel.setBright((float)(bright.getValue()/250.));
                }
            });
        bright.setBounds(1050, hight-180, 30, 110);
        panel.setBright((float)(bright.getValue()/250.));
        add(bright);
        
        }
    public void reset(){
        super.reset();
        panel.reset();
        }
    public void paint(float spikes[], int subToneCount){   
        panel.paint(spikes,subToneCount);
        }
}
