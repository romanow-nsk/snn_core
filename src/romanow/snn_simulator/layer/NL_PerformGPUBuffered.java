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

public class NL_PerformGPUBuffered extends NL_IntegrateBuffered{
    private float tmp2[];
    private int delta = 100;
    private static Kernel kernel=null;    
    public NL_PerformGPUBuffered(){
        super();
        kernel=null;      
        }
    @Override
    public void multiStep(I_NeuronStep back) throws UniException {
        long tt = System.currentTimeMillis(); 
        tmp2 = create();
        final float in[]=create();                 // МАССИВЫ ДОЛЖНЫ БЫТЬ СОЗДАНЫ ЛОКАЛЬНО
        final int size = src.size();               // ПРЯМО НЕ БЕРЕТ
        int k=0;
        for(int i=0;i<depth;i++)
            for(int j=0;j<size;j++)
                in[k++] = inData[i][j];
        final int delta2 = this.delta;              // Локальная копия
        final int steps = depth;
        final float tmp[]=create();                 // МАССИВЫ ДОЛЖНЫ БЫТЬ СОЗДАНЫ ЛОКАЛЬНО
//        if (kernel==null){                        // Из-за этого неточно считает
        kernel = new Kernel(){
            @Override
            public void run() {
                int i = getGlobalId();
                int j,k,step;
                int base=0;
                for(step=0;step<steps;step++,base += size){
                    for(j=i-delta2;j<=i+delta2;j++){
                        if (j<0 || j>=size)
                            continue;
                        for(k=i-delta2;k<=i+delta2;k++){
                            if (k<0 || k>=size)
                                continue;
                        tmp[base+i]+=(in[base+i]-in[base+j])*(in[base+i]-in[base+k])*(in[base+k]-in[base+j]);
                        }
                    }
                }
                }
            };
            if (pars.GPUmode==0)
                kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
            if (pars.GPUmode==1)
                kernel.setExecutionMode(Kernel.EXECUTION_MODE.CPU);
            if (pars.GPUmode==2)
                kernel.setExecutionMode(Kernel.EXECUTION_MODE.JTP);
//            }
        tt = System.currentTimeMillis(); 
        kernel.execute(Range.create(size));   
        System.out.println("gpu time = "+(System.currentTimeMillis()-tt));
        System.out.println("conversion time = "+kernel.getConversionTime());
        System.out.println("execution time = "+kernel.getExecutionTime());
        kernel.dispose();
        int kk=0;
        for (int step = 0; step < steps; step++) {
            outData[step] = new float[size];
            for (int i = 0; i < size; i++,kk++) {
                outData[step][i]= tmp[kk];
                }
            normalize(outData[step]);       
            }
        }
    @Override
    public void synch() {
        out = tmp2;
        }
    @Override
    public String getTypeName() {
        return "Производительность-GPU-буфер";
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
