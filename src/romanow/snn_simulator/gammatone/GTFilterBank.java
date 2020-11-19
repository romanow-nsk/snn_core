/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.gammatone;

import static romanow.snn_simulator.fft.FFT.Ccontr;
import static romanow.snn_simulator.fft.FFT.Octaves;
import static romanow.snn_simulator.fft.FFT.pow2;
import static romanow.snn_simulator.fft.FFT.sizeHZ;
import romanow.snn_simulator.fft.FFTArray;
import romanow.snn_simulator.fft.GPU;

/**
 *
 * @author romanow
 */
public class GTFilterBank {
    private GTFilter filterBank[]=null;
    private int subToneCount=0;
    private float max=0;
    int quantCount=0;
    public GTFilter[] getFilterBank() {
        return filterBank;
        }
    public void addQuantCount(int vv){
        quantCount+=vv;
        }
    public void clear() {
        quantCount=0;
        max=0;
        }
    public int size(){
        return filterBank.length;
        }
    public GTFilterBank(){
        quantCount=0;
        }
    public void createGTFilterBank(int subToneCount){   
        this.subToneCount = subToneCount;
        float c0 = Ccontr;
        int octSize = 12*subToneCount;
        int dd = 0;
        filterBank = new GTFilter[Octaves*octSize];
        int k=0;
        for(int i=0;i<Octaves;i++,c0*=2){
            for(int j=0;j<octSize;j++,k++){
                float tone = (float)(c0*Math.pow(2, ((float)j)/octSize));
                filterBank[k]=new GTFilter(sizeHZ, (int)tone);
                }
            }        
        }
    public void procCohleogramm(float wave[], int procOver, GPU gpu){
        if (gpu.devicePresent())
            procCohleogrammGPU(wave,procOver);
        else
            procCohleogrammJava(wave,procOver);
        }
    public FFTArray procCohleogrammJava(float fullwave[], int base, int lnt){
        for(int nn=0; nn<lnt; nn++){
            float vv = base + nn >=fullwave.length ? 0 : fullwave[base+nn];
            for(int j=0; j<filterBank.length;j++){
                filterBank[j].filterOne(vv);
                }
            }
        return getGTSpectrum();
        }
    public void procCohleogrammJava(float wave[], int procOver){
        int nQuant = wave.length*(100-procOver)/100;
        for(int nn=0; nn<nQuant; nn++){
            for(int j=0; j<filterBank.length;j++){
                filterBank[j].filterOne(wave[nn]);
                }
            quantCount++;
            }
        }
    public boolean valid(){ return filterBank!=null; }
    public void procCohleogrammGPU(final float wave[], int procOver){
        final int nQuant = wave.length*(100-procOver)/100;
        GPUGTFilterBank gpuFilter = new GPUGTFilterBank();
        gpuFilter.execute(filterBank,wave,nQuant);
        addQuantCount(nQuant);
        }           
    public GTFilter get(int i){
        return filterBank[i];
        } 
    public float []getGammatone(float wave[], int procOver,int note,boolean env){ // Для одного тона
        GTFilter flt = filterBank[note*subToneCount];
        //------------ Размер выхода с учетом перекрытия -----------------------
        float out[] = new float[wave.length*(100-procOver)/100];
        for(int i=0; i<out.length;i++){
            out[i] = flt.filterOne(wave[i]);
            if (env)
                out[i] = flt.getEnv();
            }
        return out;
        }
    public FFTArray getGTSpectrum(){ 
        FFTArray gt = new FFTArray(filterBank.length);
        for(int i=0; i<filterBank.length;i++){
            gt.set(i,filterBank[i].getMid());
            filterBank[i].clearMid();
            } 
        return gt;
        }
}
