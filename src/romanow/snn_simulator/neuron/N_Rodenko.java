/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_NeuronStep;

/**
 *
 * @author romanow
 */
public class N_Rodenko extends N_SingleSynapsed{
    private double sum=0,sum2=0;        // Статистика входного сигнала
    private int n=0;
    private float koeff=1;
    private float koeff2=1;
    private float prev=0;
    @Override
    public void changePotecial(I_NeuronStep Back) {  // Переопределяется в ПК
        float in = synapse.getSpike();
        sum += in;                      // Сумма амплитуд
        sum2 += in*in;                  // Сумма квалдратов амплитуд
        n++;                            // Количество отсчетов
        double nostab = (in-prev);      // Разность соседних значений амплитуд
        nostab = nostab * nostab;       // в квадрате
        sum2 += koeff2*nostab;          // добавляетсяк дисперсии с koeff2       
        double mid = sum/n;             // Среднее
        double disp = sum2/n - mid*mid; // Дисперсия - отношение среднего к ст. отклонению
        double out = (disp == 0 ? GBL.FireON : mid / Math.sqrt(disp));
        out *= koeff;                   // нормировка 0..1
        out = (out > GBL.FireON ? GBL.FireON : out);
        out = (out < GBL.FireOFF ? GBL.FireOFF : out);
        prev = (float)out;
        setOutValue(prev);
        }

    @Override
    public void reset() {
        super.reset(); 
        sum=0;
        sum2=0;
        n=0;    
    }
    
    @Override
    public String getTypeName() {
        return "Нейрон Роденко";
        }    
}
