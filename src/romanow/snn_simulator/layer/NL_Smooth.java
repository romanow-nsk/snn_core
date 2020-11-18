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

public class NL_Smooth extends NL_Integrate{
    private int width=0;
    private float v0[]=null;
    private float v1[]=null;
    private float v2[]=null;
    public NL_Smooth(int width){
        super();
        this.width = width;
        }
    public void setWidth(int width) {
        this.width = width;
        }
    public NL_Smooth(){ this(5); }
    @Override
    public void step(I_NeuronStep back) throws UniException {
        float in[] = src.getSpikes();
        v2 = create();
        for(int i=0;i<v2.length;i++){
            float sum=0;
            for(int j=i-width/2;j<=i+width/2;j++){
                int jj=j;
                if (jj<0) jj=0;
                if (jj>=v2.length) jj=v2.length-1;
                sum += in[jj];
                }
            sum/=width;
            sum += (v0[i] + v1[i])/3;
            v2[i] = (float)sum;
            }
        }
    @Override
    public void synch() {
        v0 = v1;
        v1 =  v2;
        out = v2;
        }
    @Override
    public void reset(FFTParams pars) throws UniException {
        super.reset(pars);
        v0 = create();
        v1 = create();
        v2 = create();
        }

    @Override
    public String getTypeName() {
        return "Сглаживающий";
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
