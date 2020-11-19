/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.powerline;

import java.io.IOException;
import java.util.Date;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import static romanow.snn_simulator.fft.FFT.convert;
import romanow.snn_simulator.fft.FFTArray;
import romanow.snn_simulator.fft.FFTAudioSource;
import romanow.snn_simulator.fft.FFTCallBack;
import romanow.snn_simulator.fft.TimeCounter;

/**
 *
 * @author romanow
 */
public class FFT100 {
    private float wave[]=null;                     // Текущая волна
    private FFTArray spectrum=null;                // Текущий спектр
    private int nblock=0;                           // Индекс спектра (шаг модели)
    private FFTArray spectrumList[]=null;           // preload спектр
    private float fullWave[]=null;                  // preload волна
    private Complex[] complexSpectrum=null;
    private float compressGrade=0;                  //???
    private boolean compressMode=false;             //???
    private float kAmpl=1;                          //???
    private float stepHZLinear=0.1f;
    private int W;                                  // Кол-во точек в окне
    private int sizeHZ=100;
    private int stepMS=0;
    private int procOver=0;                         // !!!!!!!!!!!!
    private FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
    public void setProcOver( int proc){ procOver = proc;}
    public void setStepHZLinear( float stepHZLinear){ this.stepHZLinear = stepHZLinear;}
    public float getStepHZLinear(){ return stepHZLinear; }
    public FFTArray []getSpectrumList(){ return spectrumList; }
    //------------------------------------------------------------------------------------
        public void fftDirectStandart(){
        long timeStart = new Date().getTime();
        complexSpectrum = fft.transform(convert(wave),TransformType.FORWARD);
     	float radice = (float)(1 / Math.sqrt(wave.length));
        for(int i = 0; i < wave.length/2 + 1; i++){
            spectrum.set(i, (float)complexSpectrum[i].abs()*radice);
            }
        }
    //----------------- Прямая конвертация спектра в массив ------------------
    public void calcFFTParams(){
        W = (int)(sizeHZ / stepHZLinear);
        while(W%1024!=0) W++;
        stepHZLinear = ((float)sizeHZ)/W;
        stepMS = 10*W*(100-procOver)/sizeHZ;
        }        
    public int getAudioLength(FFTAudioSource audioInputStream, FFTCallBack back){
        calcFFTParams();
        if (audioInputStream==null){
            back.onError(new Exception("Не выбран источник аудио"));
            return -1;
            }
        back.onMessage("Длина "+audioInputStream.getFrameLength()+
            " дискретность "+stepHZLinear+" гц");
        int size = (int)audioInputStream.getFrameLength();
        return size;
    }        
    public  boolean preloadWave(FFTAudioSource audioInputStream, FFTCallBack back){
        int size = getAudioLength(audioInputStream, back);
        if (size == -1)
            return false;
        fullWave = new float[size];
        try {
            audioInputStream.read(fullWave, 0, size);
            } catch(IOException ee){
                back.onError(new Exception("Ошибка чтения аудио"));
                return false;
                }
        return true;
        }
    public void preloadFullSpectrum(FFTAudioSource audioInputStream, FFTCallBack back){
        int nQuant = W*(100-procOver)/100;
        int sz = fullWave.length/nQuant;
        if (fullWave.length%nQuant !=0 ) sz++;
        spectrumList = new FFTArray[sz];
        wave = new float[W];        
        back.onMessage("Отсчетов спектра:"+sz);
        TimeCounter tc = new TimeCounter("Конвертация спектра");
        int dd = sz/20;
        for(int i=0, base=0; i<spectrumList.length; i++, base+=nQuant){
            spectrum = new FFTArray(wave.length);
            for(int j=0;j<W;j++){
                wave[j] = base+j<fullWave.length ? fullWave[base+j] : 0;
                }
            fftDirectStandart(); // Переменный (фикс.) размер окна
            spectrum.compress(compressMode,compressGrade,kAmpl);
            spectrumList[i] = spectrum;
            if (i%dd==0)
                back.onMessage(""+i*100/sz+"%");
                }
            }    
}
