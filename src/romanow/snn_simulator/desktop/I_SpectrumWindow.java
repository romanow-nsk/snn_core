/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import romanow.snn_simulator.fft.FFT;

/**
 *
 * @author romanow
 */
public interface I_SpectrumWindow {
    public void paint(float spikes[], int subToneCount);
    public void paint(FFT fft);
    public void paint(float[] val, String title);
    public void paint(float[] val);
    public void reset();
    public void setCallBack(LayerWindowCallBack back);
    public void repaint();
}
