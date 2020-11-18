/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

// Интегрированный слой обнаружения локальных максимумов

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_NeuronOutput;

public class NL_LocalMaxes extends NL_Integrate{
    private float tmp[]=null;
    private final float K=1.5F;             // 
    private final float K2=1;              // 
    private final int level0=100;          // 
    public NL_LocalMaxes(){
        }
    @Override
    public void step(I_NeuronStep back) throws UniException {
        tmp = create();
        float in[] = (float[])src.getSpikes().clone();
        int w=20;           // Ширина распространения разницы
        for(int i=0;i<in.length;i++){
            int s=0;
            for(int j=i-w;j<=i+w;j++){
                if (j<0 || j>=in.length || i==j)
                    continue;
                s += in[i]-in[j];
                }
            float vv = (int)(K2*(in[i]+K*s));
            if (vv>GBL.FireON)
                vv = GBL.FireON;
            tmp[i] = (float)vv;
            if (tmp[i]<0)
                tmp[i]=GBL.FireOFF;
            }
        normalize(tmp);
        }
    @Override
    public void synch() {
        out = tmp;
        }
    @Override
    public String getTypeName() {
        return "Лок.максимум";
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
