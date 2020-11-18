/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

//не содержит нейронов - ИНТЕГРАЛЬНЫЙ

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_Neuron;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_Spike;
import romanow.snn_simulator.fft.FFTParams;

public class NL_TwoOctaves extends NL_Integrate{
    private float v0[]=null;
    final static int delta=50;
    public NL_TwoOctaves(){
        }
    @Override
    public void step(I_NeuronStep back) throws UniException {
        int step = getSubToneCount()*12;
        float in[] = src.getSpikes();
        v0 = create();
        for(int i=0;i<in.length;i++){
            boolean no=false;
            int k;
            for(k=i-step; k>=0 && in[k]<in[i];k-=step);
            if (k>=0)               // Есть ранние гармоники
                continue;
            //if (i+step < in.length && in[i] < in[i+step])
            //    continue;
            if (Math.abs(in[i]-in[i+step])>delta)
                continue;
            v0[i]=GBL.FireON;
            }
        }
    @Override
    public void synch() {
        out = v0;
        }
    @Override
    public void reset(FFTParams pars) throws UniException {
        super.reset(pars);
        v0 = create();
        }
    @Override
    public String getTypeName() {
        return "Две октавы";
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
