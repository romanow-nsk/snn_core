package romanow.snn_simulator.layer;

/**
 * Created by romanow on 12.06.2017.
 */
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.fft.FFTParams;

public class NL_BeeMaxGPUBuffered extends NL_IntegrateBuffered{
    float nBees=1000;
    float dxVal = 3f;              // Смещение в сторону увеличения сигнала
    float kBack = 0.1f;            // Коэффициент возвращения на место
    float kVal = 0.1f;             // Инертность отслеживания value
    int dSize = 50;                // Часть диапазона, куда можно уходить
    int dUp = 10;                  // Интервал, где искать возрастание
    int cntUp = 3;                 // Интервал, где искать возрастание
    float fIdx[] = null;            // Индекс местоположения пчелы
    float idx0[] = null;            // Начальный индекс местоположения пчелы
    float nextIdx[] = null;         // Местоположение следующего шага
    float value[] = null;           // Собственный максимум
    float outCount[][] = null;


    public NL_BeeMaxGPUBuffered(){
        super();
        }

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
    //------------ СТАРЫЙ step оставить для образца
    /*
    public void step(int idx){
        for (int ii=0;ii<depth;ii++){
            int cIdx = (int)fIdx[idx];           // Текущий индекс
            int left = (int)idx0[idx]-dSize;
            if (left <0)
                left = 0;
            int right = (int)idx0[idx]+dSize;
            if (right>=inData[ii].length)
                right = inData[ii].length-1;
            nextIdx[idx] = fIdx[idx];
            boolean toUp=false;
            float delta = inData[ii][cIdx]-value[idx];
            value[idx] += delta * kVal;
            int cnt=0;
            for(int i=cIdx+1;i<inData[ii].length-1 && i<cIdx+dUp;i++)
                if (inData[ii][i]>inData[ii][cIdx])
                    cnt++;
            int cnt2=0;
            for(int i=cIdx-1; i>=0 && i>=cIdx-dUp;i--)
                if (inData[ii][i]>inData[ii][cIdx])
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
            outCount[ii][(int)fIdx[idx]] +=1;
            fIdx[idx] = nextIdx[idx];
        }
    }
    */
    //------------------------------------------------------------------------------------

    //-------------------------------------------------------------------------------------
    @Override
    public void multiStep(I_NeuronStep back) throws UniException {
        long t0 = System.currentTimeMillis();
        for (int ii=0;ii<depth;ii++){
            for(int i=0;i<outCount[ii].length;i++)
                outCount[ii][i]=0;
            }
        //for(int i=0;i<idx0.length;i++)              // Последоввательный ПАРАЛЛЕЛИЗМ
        //    step(i);
        new BeeMaxGPUKernel().parStep(this);      // ПАРАЛЛЕЛЬНЫЙ ПАРАЛЛЕЛИЗМ !!!!!
        for (int ii=0;ii<depth;ii++){
            float max=outCount[ii][0];
            for(int i=0;i<outCount.length;i++){
                if (outCount[ii][i]>max)
                    max = outCount[ii][i];
            }
            out = new float[outCount[ii].length];
            for(int i=0;i<outCount[ii].length;i++)      // Выход пропорционально концентрации пчёл
                out[i]=(outCount[ii][i])*GBL.FireON/max;
            outData[ii] = out;
        }
        System.out.println("time:"+(System.currentTimeMillis()-t0));
    }
    //-------------------------------------------------------------------------
    //@Override
    //public void synch() {
    //    for(int i=0;i<nextIdx.length;i++)
    //        fIdx[i] = nextIdx[i];
    //    }
    @Override
    public void reset(FFTParams pars) throws UniException {
        super.reset(pars);
        int kBees = 2;
        int sizeLayer = size();
        fIdx = createFloatIdx((int)nBees,sizeLayer);
        idx0 = createFloatIdx((int)nBees,sizeLayer);
        outCount = new float[depth][];
        for(int i=0;i<depth;i++)
            outCount[i] = createFloat(sizeLayer);
        nextIdx = createFloat((int)nBees);
        value = createFloat((int)nBees);
    }

    @Override
    public String getTypeName() {
        return "Макси-пчёлы-GPU";
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