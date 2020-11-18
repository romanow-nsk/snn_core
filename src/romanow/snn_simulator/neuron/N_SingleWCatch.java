/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.Token;
import romanow.snn_simulator.UniException;

//--- Алгоритм обучения STDP - в зависимсоти от соотношения моментв входных и выходных спайков
public class N_SingleWCatch extends N_SingleCatch{
    private float kSens=1;                              // Чувствительность ловушки
    private float W0=0.5f;
    private float DW=0.01f;    
    private float weight[] = new float[0];              // Вес синапса, <0 - тормозящие
    private int timeStamp=1;                            // Отметка времени локальная
    private int timeInp[] = new int[0];                 // Отметки последнего спайка
    private int overW0=3;                               // Превышение чувствительности
    @Override
    public void addInputsLinear(int idx, int size, I_Layer src) throws UniException{
        int cnt = GBL.addInputsLinear(synapses, idx, size, src);
        weight = GBL.extend(weight, cnt, 1);
        }
    @Override
    public void reset() {
        super.reset(); 
        timeStamp = 0;
        timeInp = new int[size()];
        for(int i=0;i<timeInp.length;i++)
            timeInp[i]=0;
            }
    @Override
    public void addInputsOctave(int idx, int octaveStep, int size, I_Layer src) throws UniException {
        int cnt = GBL.addInputsOctave(synapses, idx, octaveStep, size, src);
        weight = GBL.extend(weight, cnt, 1);
        } 
    public void setWeight(int id, float val){
        if (id<0 || id>=weight.length)
            return;        
        weight[id] = val;
        }
    public void setWeight(float val){
        for (int i=0;i<weight.length;i++)
            weight[i] = val;
        }
    public void changeWeight(int id, float val){
        weight[id] += val;
        }
    public float getWeight(int id){
        if (id<0 || id>=weight.length)
            return 0;        
        return weight[id];
        }
    public void addSynapse(I_NeuronOutput in, float w){
        synapses.add(in);
        weight = GBL.extend(weight, w);
        }
    public void addSynapse(I_NeuronOutput in, boolean withWeight){
        synapses.add(in);
        if (withWeight)
            weight = GBL.extend(weight, 1);
        }
    //--------------------- Сохранение с весами
    public String save() throws IOException {
        String ss = super.save()+"/"+kSens+"/"+W0+"/"+DW;
        ss+="/"+weight.length;
        for(int i=0;i<weight.length;i++)
            ss+="/"+weight[i];
        return ss;
        }
    public void load(Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        super.load(in, map);
        kSens = in.getFloat();
        W0 = in.getFloat();
        DW = in.getFloat();
        int sz = in.getInt();
        weight = new float[sz];
        for(int i=0;i<sz;i++)
            weight[i]=in.getFloat();
        }
    public String getFormatLabel() {
        return super.getFormatLabel()+"/kSens/W0/DW/size{w[i]}";
        }  
    @Override
    public void changePotecial(I_NeuronStep Back) {
        super.changePotecial(Back);
        timeStamp++;
        for(int i=0;i<size();i++){
            float in=get(i).getSpike();     // Допущение - на входе float
            if (this.isLearningMode() && in == GBL.FireON){
                timeInp[i] = timeStamp;     // Запомнить отметку времени входа
                }
            }
        }
    @Override
    public void onFire(I_NeuronStep back) {
        super.onFire(back); 
        if (!this.isLearningMode())
            return;
        //------ Обучение STDP - последний +DW, первый -DW, остальные в линейной пропорции
        int c1=0;
        int c2=0;
        for(int i=0;i<size();i++){
            if (weight[i]>overW0*W0 || weight[i]<=0)
                continue;
            if (timeInp[i]==0)              // Если не было
                continue;
            if (timeInp[i]<timeStamp/2)
                c1++;
            else
                c2++;
            float dd = 2*timeStamp*DW*(timeInp[i]-timeStamp/2);
            this.changeWeight(i, dd);       // В интервале +/- DW       
            }
        if (c1+c2!=0)
            back.onMessage(GBL.MesInfo,""+getUID()+" -"+c1+" +"+c2);
        timeStamp = 0;
        for(int i=0;i<timeInp.length;i++)
            timeInp[i]=0;
        }
    
    
}
