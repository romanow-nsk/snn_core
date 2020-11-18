/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

// Интегрированный слой обнаружения локальных максимумов

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.Token;

public class NL_PerformGPU extends NL_Integrate{
    private int delta = 100;
    private float tmp2[];
    private static Kernel kernel=null;    
    public NL_PerformGPU(){
        kernel=null;
        }
    @Override
    public void step(I_NeuronStep back) throws UniException {
        long tt = System.currentTimeMillis(); 
        tmp2 = create();
        final float in[]=new float[size()];        // МАССИВЫ ДОЛЖНЫ БЫТЬ СОЗДАНЫ ЛОКАЛЬНО
        final int size = in.length;                // ПРЯМО НЕ БЕРЕТ
        float xx[] = src.getSpikes();
        for(int i=0; i<size;i++)
            in[i] = xx[i];
        final float tmp[]=new float[size()];        // МАССИВЫ ДОЛЖНЫ БЫТЬ СОЗДАНЫ ЛОКАЛЬНО
        final int delta2 = this.delta;               // Локальная копия
        //if (kernel==null){                        // Из-за этого неточно считает
            kernel = new Kernel(){
            @Override
            public void run() {
                int i = getGlobalId();
                int j,k;
                tmp[i]=0;
                for(j=i-delta2;j<=i+delta2;j++){
                if (j<0 || j>=size)
                    continue;
                    for(k=i-delta2;k<=i+delta2;k++){
                        if (k<0 || k>=size)
                            continue;
                        tmp[i]+=(in[i]-in[j])*(in[i]-in[k])*(in[k]-in[j]);
                        }
                    }
                }
            };
            kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
//            }
        tt = System.currentTimeMillis(); 
        kernel.execute(Range.create(size));   
        System.out.println("gpu time = "+(System.currentTimeMillis()-tt));
        System.out.println("conversion time = "+kernel.getConversionTime());
        System.out.println("execution time = "+kernel.getExecutionTime());
        kernel.dispose();
        normalize(tmp);
        for (int i = 0; i < size; i++) {
            tmp2[i]=tmp[i];
            }
        //for (int i = 0; i < size; i++) {
        //    if (tmp[i]!=tmp2[i])
        //        System.out.printf("%d + %6.2f = %8.2f\n", i, tmp[i], tmp2[i]);
        //        }

        }
    @Override
    public void synch() {
        out = tmp2;
        }
    @Override
    public String getTypeName() {
        return "Производительность-GPU";
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
