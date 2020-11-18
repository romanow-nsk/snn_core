/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.neuron;

import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.Token;

/**
 *
 * @author romanow
 */
//--------- НЕЙРОН ГАВРИЛОВА ---------------------------------------------------
// Алгоритм обучения STDP - 
public class N_Gavrilov extends N_SingleWCatch{
    private float R=0.1f;
    private float H=0.8f;
    private float HMax=1f;
    private float HMin=0.7f;
    private float DH=0.05f;
    
    @Override
    public String save() throws IOException {
        String ss = super.save()+"/"+R+"/"+H+"/"+HMax+"/"+HMin+"/"+DH;
        return ss;
    }

    @Override
    public void load(Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        super.load(in, map); 
        R = in.getFloat();
        H = in.getFloat();
        HMax = in.getFloat();
        HMin = in.getFloat();
        DH = in.getFloat();
        int sz = in.getInt();
    }

    @Override
    public String getFormatLabel() {
        return super.getFormatLabel()+"/R/H/Hmax/Hmin/DH";
        }
    
    public N_Gavrilov(){
        }
    @Override
    public void reset() {
        super.reset();
        H = HMax;
        }
    public float getR() { return R; }
    public void setR(float R) { this.R = R; }
    public float getH() { return H; }
    public void setHMax(float H) { this.HMax = H; }
    public void setHMin(float H) { this.HMin = H; }
    public void setDH(float H) { this.DH = H; }
    @Override
    public void changePotecial(I_NeuronStep back){
        super.changePotecial(back);
        float sum=0;
        for(int i=0;i<size();i++){
            float in=get(i).getSpike();     // Допущение - на входе float
            sum += in*R*getWeight(i);
            }
        this.addPotential(sum);
        if (H > HMin)
            H-=DH;
        setLevel(H);
        }  
    @Override
    public void onFire(I_NeuronStep back) {                  // Событие - при срабатывании
        super.onFire(back);
        H = HMax;
        }
    @Override
    public String getTypeName() {
        return "Нейрон Гаврилова";
        }
    public static void main(String argv[]){
        N_Gavrilov nn = new N_Gavrilov();
        N_Random n1 = new N_Random();
        N_Random n2 = new N_Random();
        nn.addSynapse(n1, 1);
        nn.addSynapse(n2, 1);
        nn.reset();
        for(int i=0;i<1000;i++){
            final int ii=i;
            n1.step(new I_NeuronStep(){
                @Override
                public void onFire(N_BaseNeuron nw) {
                    System.out.println("Шаг "+ii+" n1 сработал");
                    }
                @Override
                public void onMessage(int level, String mes) {
                    }
                });
            n2.step(new I_NeuronStep(){
                @Override
                public void onFire(N_BaseNeuron nw) {
                    System.out.println("Шаг "+ii+" n2 сработал");
                    }
                @Override
                public void onMessage(int level, String mes) {
                    }
                });
            nn.step(new I_NeuronStep(){
                @Override
                public void onFire(N_BaseNeuron nw) {
                    System.out.println("Шаг "+ii+" nn сработал");
                    }
                @Override
                public void onMessage(int level, String mes) {
                    }
                }); 
            n1.synch();
            n2.synch();
            nn.synch();
            }
        }
}
