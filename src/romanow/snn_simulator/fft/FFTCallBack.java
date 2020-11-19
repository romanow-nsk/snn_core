/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import romanow.snn_simulator.desktop.I_Notify;

/**
 *
 * @author romanow
 */
public interface FFTCallBack extends I_Notify {
    public void onStart(float stepMS);
    public void onFinish();
    public boolean onStep(int nBlock, int calcMS, float totalMS, FFT fft);
    }
