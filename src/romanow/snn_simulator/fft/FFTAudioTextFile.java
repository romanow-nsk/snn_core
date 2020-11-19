/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioSystem;

/**
 *
 * @author romanow
 */
public class FFTAudioTextFile implements FFTFileSource{
    private String fspec=null;
    private BufferedReader AudioFile=null;
    private int sz=0;
    private float data[]=null;
    private int cnum;
    private int repeat=100;     // Повторять по кругу
    @Override
    public void enableToPlay(boolean play) {
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
    public void play(int start, int delay) {
        }
    @Override
    public void pause() {
        }
    public void write_little_endian(int word, int num_bytes, OutputStream wav_file) throws IOException{
        int buf;
        while(num_bytes>0){   
            buf = word & 0xff;
            wav_file.write(buf);
    		num_bytes--;
            word >>= 8;
            }
        }       
    public void write_string(String ss, OutputStream wav_file) throws IOException{
        char cc[] = ss.toCharArray();
        for(int i=0;i<cc.length;i++){   
            wav_file.write((byte)cc[i]);
            }
        }       
    public boolean convertToWave(String PatnToFile,FFTCallBack back){
        fspec=null;
        try {
            AudioFile = new BufferedReader(new FileReader(PatnToFile));
            } catch (FileNotFoundException ex) {
                return false;
                }
        String in;
        try {
            for(int i=0;i<10;i++)           // 10 первых строк пропустить
                in = AudioFile.readLine();
            int num_samples = Integer.parseInt(AudioFile.readLine());
            short data[] = new short[num_samples];
            float mid=0,min=data[0],max=data[0];
            for(int i=0;i<num_samples;i++){
                data[i]=(short)Integer.parseInt(AudioFile.readLine());
                mid += data[i];
                }
            int midd = (int)(mid/num_samples);
            for(int i=0;i<num_samples;i++){         // Убрать постоянную составляющую
                data[i] -= midd;
                }
            for(int i=0;i<num_samples;i++){
                if (data[i]>max) max=data[i];
                if (data[i]<min) min=data[i];
                }
            if (Math.abs(max)>Math.abs(min))
                min = max;
            min = Math.abs(min);
            min = Short.MAX_VALUE*0.9f/min;
            for(int i=0;i<num_samples;i++){
                data[i] *= min;
                }
            int k = PatnToFile.lastIndexOf(".");
            String outname = PatnToFile.substring(0, k)+".wav";
            FileOutputStream wav_file = new FileOutputStream(outname);
        	int sample_rate;
            int num_channels;
            int bytes_per_sample;
            int byte_rate;
            int i;  
            num_channels = 1;  
            bytes_per_sample = 2;
            sample_rate = 100; // 44100;
        	byte_rate = sample_rate*num_channels*bytes_per_sample;
        	write_string("RIFF", wav_file);
            write_little_endian(36 + bytes_per_sample* num_samples*num_channels, 4, wav_file);
            write_string("WAVE", wav_file);
            write_string("fmt ", wav_file);
            write_little_endian(16, 4, wav_file);   
            write_little_endian(1, 2, wav_file);    
            write_little_endian(num_channels, 2, wav_file);
            write_little_endian(sample_rate, 4, wav_file);
            write_little_endian(byte_rate, 4, wav_file);
            write_little_endian(num_channels*bytes_per_sample, 2, wav_file);  
            write_little_endian(8*bytes_per_sample, 2, wav_file);  
            write_string("data", wav_file);
            write_little_endian(bytes_per_sample* num_samples*num_channels, 4, wav_file);
            for (i=0; i< num_samples; i++){
                write_little_endian(data[i],bytes_per_sample, wav_file);
                }   
            wav_file.flush();
            wav_file.close();
            back.onMessage("Записано "+num_samples+" сэмплов, "+ ((float)num_samples)/sample_rate+ " сек");
            fspec = PatnToFile;
            return true;
            } catch(Exception ee){
                back.onError(ee.getMessage());
                close();
                return false;
                }
        }        
    public final static int Test=0;
    public final static int Open=1;
    public final static int OpenAndPlay=2;
    public boolean testAndOpenFile(int mode, String PatnToFile, int sizeHZ, FFTCallBack back){
        try {
            AudioFile = new BufferedReader(new FileReader(PatnToFile));
            } catch (FileNotFoundException ex) {
                return false;
                }
        if (mode==Test){
            close();
            return true;
            }
        String in;
        try {
            do {
                in = AudioFile.readLine();
                }
                while (in!=null && in.startsWith("//"));
            if (in==null){
                close();
                return false;
                }
            sz = Integer.parseInt(AudioFile.readLine());
            if (mode == OpenAndPlay){
                data = new float[sz];
                for(int i=0;i<sz;i++)
                    data[i]=((float)Integer.parseInt(AudioFile.readLine()))/Short.MAX_VALUE;
                }
            close();
            cnum=0;
            return true;
            } catch(Exception ee){
                close();
                return false;
                }
        }

    @Override
    public String getFileSpec() {
        return fspec;
        }

    @Override
    public String testSource(int sizeHZ) {
        // Формат не проверяется
        return null;
        }    
    @Override
    public long getFrameLength() {
        return sz*repeat;
        }
    @Override
    public int read(float[] buf, int offset, int lnt) throws IOException {
        if (sz==0)
            return 0;
        for(int i=offset; i<offset+lnt; i++){
            buf[i] = data[cnum%sz];
            cnum++;
            }
        return lnt;
        }
    @Override
    public void close() {
        try {
            if (AudioFile!=null){
                AudioFile.close();
                }
            } catch (IOException ex) {}
        }
    @Override
    public String getTypeName() {
        return "Текстовый файл";
        }
    @Override
    public String getName() {
        return getTypeName();
        }

    @Override
    public int getSampleRate() {
        return 100;
        }
}