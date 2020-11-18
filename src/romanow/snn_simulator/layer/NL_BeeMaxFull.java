/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.fft.FFTParams;

public class NL_BeeMaxFull extends NL_Integrate{
    float nBees = 1000;            // Пчел
    float dxVal = 3f;              // Смещение в сторону увеличения сигнала
    float kBack = 0.1f;            // Коэффициент возвращения на место
    float kVal = 0.1f;             // Инертность отслеживания value
    int dSize = 50;                // Часть диапазона, куда можно уходить    
    int dUp = 10;                  // Интервал, где искать возрастание
    int cntUp = 3;                 // Интервал, где искать возрастание
    private float fIdx[] = null;   // Индекс местоположения пчелы
    private float idx0[] = null;   // Начальный индекс местоположения пчелы
    private float nextIdx[] = null;// Местоположение следующего шага
    private float value[] = null;  // Собственный максимум
    private float outCount[] = null;
    private int []createInt(int size){
        int out[]=new int[size];
        for(int i=0;i<out.length;i++)
            out[i]=0;
        return out;
        }
    private float []createFloatIdx(int size, int size0){
        float out[]=new float[size];
        for(int i=0;i<out.length;i++)
            out[i]=i*size0/size;
        return out;
    }
    private float []createFloat(int size){
        float out[]=new float[size];
        for(int i=0;i<out.length;i++)
            out[i]=0;
        return out;
        }
    public void step(float spikes[],int idx){
        int cIdx = (int)fIdx[idx];           // Текущий индекс
        int left = (int)idx0[idx]-dSize;
        if (left <0)
            left = 0;
        int right = (int)idx0[idx]+dSize;
        if (right>=spikes.length)
            right = spikes.length-1;
        nextIdx[idx] = fIdx[idx];
        boolean toUp=false;
        float delta = spikes[cIdx]-value[idx];
        value[idx] += delta * kVal;
        int cnt=0;
        for(int i=cIdx+1;i<spikes.length-1 && i<cIdx+dUp;i++)
            if (spikes[i]>spikes[cIdx])
                cnt++;
        int cnt2=0;
        for(int i=cIdx-1; i>=0 && i>=cIdx-dUp;i--)
            if (spikes[i]>spikes[cIdx])
                cnt2++;
        if (cnt > 3){
            nextIdx[idx] += dxVal; // Движение в сторону роста сигнала
            toUp=true;
            }
        if (cnt2 > 3){
            nextIdx[idx] -= dxVal;
            toUp=true;
            }
        if (delta > 0 || toUp){
            }
        else{
            nextIdx[idx] -= (cIdx-idx0[idx])*kBack;
            }
        if (nextIdx[idx]<left)
            nextIdx[idx]=left;
        if (nextIdx[idx]>right)
            nextIdx[idx]=right;
        }
    //-------------------------------------------------------------------------------------    
    @Override
    public void step(I_NeuronStep back) throws UniException {
        float in[] = src.getSpikes();
        for(int i=0;i<idx0.length;i++)
            step(in, i);
        for(int i=0;i<outCount.length;i++)
            outCount[i]=0;
        for(int i=0;i<idx0.length;i++)              // "Гистограмма" пчел по диапазону
            outCount[(int)fIdx[i]] +=1;
        float max=outCount[0];
        for(int i=0;i<outCount.length;i++){
            if (outCount[i]>max)
                max = outCount[i];
            }
        for(int i=0;i<outCount.length;i++)          // Выход пропорционально концентрации пчёл
            out[i]=(outCount[i])*GBL.FireON/max;
        }
    @Override
    public void synch() {
        for(int i=0;i<nextIdx.length;i++)
            fIdx[i] = nextIdx[i];
        }
    @Override
    public void reset(FFTParams pars) throws UniException {
        super.reset(pars);
        int sizeLayer = size();
        fIdx = createFloatIdx((int)nBees,sizeLayer);
        idx0 = createFloatIdx((int)nBees,sizeLayer);
        outCount = createFloat(sizeLayer);
        nextIdx = createFloat((int)nBees);
        value = createFloat((int)nBees);
        }

    @Override
    public String getTypeName() {
        return "Макси-пчёлы-2";
        }
    @Override
    public void save(BufferedWriter out) throws IOException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }
    @Override
    public String save() throws IOException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return null;
        }
    @Override
    public void load(romanow.snn_simulator.Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }
    @Override
    public String getFormatLabel() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return null;
        }
}
