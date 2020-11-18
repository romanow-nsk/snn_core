/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import java.io.IOException;
import romanow.snn_simulator.I_TypeName;

/**
 *
 * @author romanow
 */
public interface FFTAudioSource extends I_TypeName{
    public String testSource(int sizeHZ);
    public long getFrameLength();
    public int read(float buf[], int offset, int lnt) throws IOException;
    public void close() throws IOException;
    public void enableToPlay(boolean play);
    public void play(int start, int delay);
    public void pause();
    public boolean isPlaying();
    public int getCurrentPlayTimeMS();
    public int getSampleRate();
}
