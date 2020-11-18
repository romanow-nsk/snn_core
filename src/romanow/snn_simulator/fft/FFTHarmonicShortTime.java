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
public class FFTHarmonicShortTime extends FFTHarmonic{
    int soundTime=0;
    int pauseTime=0;
    int counter=0;
    public FFTHarmonicShortTime(int hz, int sec, int amplProc, int ngarm,int soundTimeMs, int pauseTimeMs){
        super(hz,sec,amplProc,ngarm);
        this.soundTime = soundTimeMs * FFT.sizeHZ /1000;
        this.pauseTime = pauseTimeMs * FFT.sizeHZ /1000;
        counter = this.pauseTime;
        }
    public FFTHarmonicShortTime(){ this(160,10,75,7,300,3000); }
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
            if (counter >0){
                counter--;
                if (counter==0)
                    counter = -this.soundTime;
                }
            else{
                counter++;
                if (counter==0)
                    counter = this.pauseTime;
                for(int j=0;j<ngarm;j++,s*=2)
                    sum += Math.sin(2*Math.PI * (cnum % (period/s))/(period/s));
                sum *= ampl/ngarm;
                }
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
        return "5 октав с паузой";
        }

}
