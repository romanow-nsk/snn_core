/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

/**
 *
 * @author romanow
 */
public class FFTAudioFile implements FFTFileSource{
    private String fspec=null;
    private AudioInputStream audioInputStream=null;
    private AudioFormat format=null;
    private Clip clip=null;
    private boolean isPlaying=false;
    private final static int playDiffMS = 100;      // Рассогласование воспроизведения
    //private OutputDataPlayer player = new OutputDataPlayer(20);
    @Override
    public void enableToPlay(boolean play) {
        isPlaying = play;
        }
    @Override
    public boolean isPlaying(){
        return isPlaying && clip!=null;
        }
    @Override
    public int getCurrentPlayTimeMS(){
        return !isPlaying() ? 0 : (int)(clip.getMicrosecondPosition()/1000);
        }
    @Override
    public void play(int start, int delay) {
        if (!isPlaying || clip==null)
            return;
        int pos = (int)(clip.getMicrosecondPosition()/1000);
        int dd = pos - start;
        if (Math.abs(dd)>playDiffMS){
            clip.setMicrosecondPosition(start*1000);
            System.out.println("start="+start+" diff="+dd);
            }
        clip.loop(delay);
        }
    @Override
    public void pause() {
        if (clip!=null)
            clip.stop();
        }
   
    public final static int Test=0;
    public final static int Open=1;
    public final static int OpenAndPlay=2;
    @Override
    public boolean testAndOpenFile(int mode, String PatnToFile,int sizeHZ, FFTCallBack back){
        fspec=null;
        File AudioFile = new File(PatnToFile);
        try {
            audioInputStream = AudioSystem.getAudioInputStream(AudioFile);
            } catch (Exception e) {
                back.onError(e);
                close();
                back.onFinish();
                return false;
                }
        String ss = testSource(sizeHZ);
        if (ss!=null){
            back.onError(new Exception(ss));
            back.onFinish();
            return false;
            }
        if (mode == Test)
            close();
        if (mode == OpenAndPlay){
            try {                       // Для воспроизведения нужен отдельный аудиопоток
                clip = AudioSystem.getClip();
            	clip.open(AudioSystem.getAudioInputStream(AudioFile));
                } catch (Exception ex) {
                    close();
                    back.onError(ex);
                    back.onFinish();
                    return false;
                    }
            }
        fspec = PatnToFile;
        return true;
        }

    @Override
    public String getFileSpec() {
        return fspec;
        }

    @Override
    public String testSource(int sizeHZ) {
        format = audioInputStream.getFormat();
        //DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        //DataLine.Info info = new DataLine.Info(Clip.class, format);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);   // НАДО ТАК
        if (format.getSampleRate() != sizeHZ) {
            close();
            return ""+format.getSampleRate()+" гц не поддерживается";
            }
        if (format.getChannels()!=1 || format.getSampleSizeInBits()!=16){
            close();
            return "Только 16-бит моно";
            }
        //Line.Info[] i1 = AudioSystem.getSourceLineInfo(info);
        //Line.Info[] i2 = AudioSystem.getTargetLineInfo(info);
        //Line ll = null;
        //try {
        //    ll = AudioSystem.getLine(info);
        //   } catch (LineUnavailableException e) {}
        if (!AudioSystem.isLineSupported(info)) {
            close();
            return "Формат не поддерживается";
            }
        return null;
        }    
    @Override
    public long getFrameLength() {
        return audioInputStream==null ? 0 : audioInputStream.getFrameLength();
        }
    @Override
    public int read(float[] buf, int offset, int lnt) throws IOException {
        if (audioInputStream==null) 
            return 0;
        byte bb[] = new byte[lnt*2];
        audioInputStream.read(bb);
        //player.addToPlay(bb);
        int l = lnt;
        for(int i=0; l--!=0; i++, offset++){
            float vv = (float)(((0xFF & bb[2 * i + 1]) << 8) | (0xFF & bb[2 * i]));
            buf[offset] = ((float) vv) /Short.MAX_VALUE;
            }
        return lnt;
        }
    @Override
    public void close() {
        try {
            if (audioInputStream!=null)
                audioInputStream.close();
            if (clip!=null){
                clip.close();
                }
            } catch (IOException ex) {}
        }
    @Override
    public String getTypeName() {
        return "Файл";
        }
    @Override
    public String getName() {
        return getTypeName();
        }

    @Override
    public int getSampleRate() {
        return (int)audioInputStream.getFormat().getSampleRate();
        }
}
