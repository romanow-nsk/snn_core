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
public class FFTSinus implements FFTAudioSource{
    long size;              // Кол-во отсчетов
    long cnum=0;            // текущий номер отсчета
    int period=0;           // период в отсчетах
    float ampl=0;             // амплитуда
    public FFTSinus(int hz, int sec, int amplProc){
        period = FFT.sizeHZ/hz;
        size = FFT.sizeHZ * sec;
        ampl = amplProc / 100.F;
        }
    public FFTSinus(){ this(440,40,75); }
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
            float phase = (float)(2*Math.PI * (cnum % period)/period);
            buf[offset++]= (float)Math.sin(phase)*ampl;
            //System.out.println(aa);
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
        return "440(A1)";
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
