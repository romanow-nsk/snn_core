/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

/**
 *
 * @author romanow
 */
public interface FFTCallBack {
    public void onStart(float stepMS);
    public void onFinish();
    public boolean onStep(int nBlock, int calcMS, float totalMS, FFT fft);
    public void onError(String mes);
    public void onMessage(String mes);
}
