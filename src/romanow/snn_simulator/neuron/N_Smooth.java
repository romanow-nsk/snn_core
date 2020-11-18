package romanow.snn_simulator.neuron;

import romanow.snn_simulator.I_NeuronStep;

/**
 *
 * @author romanow
 */
// 1. Числовой выхож
// 2. Сглаживание по ширине входов
// 3. Сглаживание по времени - 3 шага
public class N_Smooth extends N_SingleCatch{
    private static float K=2;
    private int v0=0;
    private int v1=0;
    public N_Smooth(){}
    @Override
    public void changePotecial(I_NeuronStep Back) {  // Переопределяется в ПК
        float sum=0;
        for (int i=0;i<size();i++){ 
            sum += get(i).getSpike();
            }
        sum/=size();
        sum += (v0 + v1)/3;
        v0 = v1;
        v1 = (int)sum;
        float out = (float)(sum*K);
        setOutValue(out);
        }
    @Override
    public String getTypeName() {
        return "Сглаживающий";
        }    
    }
