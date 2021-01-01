/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import java.io.IOException;

/**
 *
 * @author romanow
 */
public class FFTHarmonic implements FFTAudioSource{
    long size;              // Кол-во отсчетов
    long cnum=0;            // текущий номер отсчета
    float period=0;         // период в отсчетах
    float ampl=0;           // амплитуда
    int ngarm=1;
    public FFTHarmonic() { this(256,20,75,7); }
    public FFTHarmonic(int hz, int sec, int amplProc, int ngarm){
        period = FFT.sizeHZ/hz;
        size = FFT.sizeHZ * sec;
        ampl = (float)(amplProc / 100.);
        this.ngarm = ngarm;
        }
    @Override
    public String testSource(int sizeHZ) {
        return null;
        }

    @Override
    public long getFrameLength() {
        return size;
        }
    @Override
    public int read(float[] buf, int offset, int lnt) throws IOException {
        int l = lnt;                // в отсчетах
        while(l--!=0){
            int s=1;
            float sum=0;
            for(int j=0;j<ngarm;j++,s*=2)
                sum += Math.sin(2*Math.PI * (cnum % (period/s))/(period/s));
            sum *= ampl/ngarm;
            //System.out.println(aa);
            buf[offset++] = sum;
            cnum++;
            }
        return lnt;
        }
    @Override
    public void close() throws IOException {
        }
    @Override
    public void enableToPlay(boolean play) {
        }
    @Override
    public void play(int start, int delay) {
        }
    @Override
    public void pause() {
        }
    @Override
    public boolean isPlaying(){
        return false;
        }
    @Override
    public int getCurrentPlayTimeMS(){
        return 0;
        }

    @Override
    public String getTypeName() {
        return "7 октав";
        }

    @Override
    public String getName() {
        return getTypeName();
        }

    @Override
    public int getSampleRate() {
        return 44100;
        }

}
