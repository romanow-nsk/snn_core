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
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.Token;

public class NL_PerformBuffered extends NL_IntegrateBuffered{
    private float tmp2[];
    private int delta = 100;

    @Override
    public void multiStep(I_NeuronStep back) throws UniException {
        long tt = System.currentTimeMillis(); 
        tmp2 = create();
        for(int ii=0;ii<depth;ii++){       // ПОВТОРЯТЬ ДЛЯ ВСЕГО БЛОКА
            float in[] = inData[ii];
            for(int i=0;i<in.length;i++){
                tmp2[i]=0;
                for(int j=i-delta;j<=i+delta;j++){
                    if (j<0 || j>=in.length)
                        continue;
                    for(int k=i-delta;k<=i+delta;k++){
                        if (k<0 || k>=in.length)
                            continue;
                        tmp2[i]+=(in[i]-in[j])*(in[i]-in[k])*(in[k]-in[j]);
                        }
                    }
                }
            normalize(tmp2);
            outData[ii] = tmp2;
            }
        System.out.println("время цикла = "+(System.currentTimeMillis()-tt));
        }
    @Override
    public String getTypeName() {
        return "Производительность-буфер";
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
    public void load(Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFormatLabel() {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        return null;
    }
}
