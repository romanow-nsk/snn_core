/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

// Слой сглаживания = не содержит нейронов - ИНТЕГРАЛЬНЫЙ

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.fft.FFTParams;

public class NL_BeeMax extends NL_Integrate{
    float nBees = 1000;            // Пчел
    float dxVal = 3f;              // Смещение в сторону увеличения сигнала
    float kBack = 0.1f;            // Коэффициент возвращения на место
    float kVal = 0.1f;             // Инертность отслеживания value
    int dSize = 50;                // Часть диапазона, куда можно уходить    
    int dUp = 10;                  // Интервал, где искать возрастание
    int cntUp = 3;                 // Интервал, где искать возрастание
    BeeMaxParams params = null;
    BeeMax[] bees = null;
    private float outCount[] = null;
    @Override
    public void step(I_NeuronStep back) throws UniException {
        float in[] = src.getSpikes();	// Вектор входных сигналов
        for(int i=0;i<bees.length;i++)
            bees[i].step(in, params);	// Моделирование поведение всех пчёл
        for(int i=0;i<outCount.length;i++)
            outCount[i]=0;
        for(int i=0;i<bees.length;i++)  		// "Гистограмма" пчел по диапазону
            //outCount[bees[i].getIdx()] += bees[i].getValue();
            outCount[bees[i].getIdx()] +=1;		// outCnt - счетчик пчёл на частоте спектра
        float max=outCount[0];
        for(int i=0;i<outCount.length;i++){
            if (outCount[i]>max)
                max = outCount[i];			// Максимум для нормлизации
            }
        for(int i=0;i<outCount.length;i++)       // Выход пропорционально концентрации пчёл
            out[i]=(outCount[i])*GBL.FireON/max;
            //out[i]=outCount[i]<max-maxLevel ? GBL.FireON/max*outCount[i] : GBL.FireON;
        }
    @Override
    public void synch() {
        for(int i=0;i<bees.length;i++)
            bees[i].synch();
        }
    @Override
    public void reset(FFTParams pars) throws UniException {
        super.reset(pars);
        bees = new BeeMax[(int)nBees];                // Пчёл в 2 раза больше !!!!!!
        for(int i=0;i<bees.length;i++)
            bees[i] = new BeeMax((int)(i*size()/nBees));
        outCount = new float[size()];
        params = new BeeMaxParams(dxVal,kBack,kVal,dSize,dUp,cntUp);
        }

    @Override
    public String getTypeName() {
        return "Макси-пчёлы";
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
