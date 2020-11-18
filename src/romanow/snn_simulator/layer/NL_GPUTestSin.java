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

public class NL_GPUTestSin extends NL_Integrate{
    private float tmp2[];
    private static Kernel kernel=null;
    public NL_GPUTestSin(){
        kernel=null;
        }
    public static float calc(float qcos, float qsin){
        float coscf = qcos;
        float sincf = qsin;
        float ss=0;
        float oldcs=0;
        for(int i=0;i<1000000;i++){
            oldcs = qcos;
            qcos = coscf * oldcs + sincf * qsin;
            qsin = coscf * qsin - sincf * oldcs;
            ss+=qsin;
            }
        return ss;
        }
    @Override
    public void step(I_NeuronStep back) throws UniException {
        tmp2 = create();
        final float in[]=new float[size()];        // МАССИВЫ ДОЛЖНЫ БЫТЬ СОЗДАНЫ ЛОКАЛЬНО
        final int size = in.length;                // ПРЯМО НЕ БЕРЕТ
        final float tmp[]=new float[size()];        // МАССИВЫ ДОЛЖНЫ БЫТЬ СОЗДАНЫ ЛОКАЛЬНО
        final float[] cos0 = new float[size];
        final float[] sin0 = new float[size];
        for (int i = 0; i < size; i++) {
            cos0[i] = (float)Math.cos (in[i]);
            sin0[i] = (float)Math.sin (in[i]);
            }
        long tt = System.currentTimeMillis(); 
        for (int i = 0; i < size; i++) {
            tmp2[i] = calc(cos0[i],sin0[i]);
            }
        System.out.println("Without GPU="+(System.currentTimeMillis()-tt));
        normalize(tmp2);
        if (kernel==null){
            kernel = new Kernel(){
            @Override public void run() {
                int gid = getGlobalId();
                float coscf = cos0[gid];
                float sincf = sin0[gid];
                float qcos = cos0[gid];
                float qsin = sin0[gid];
                float ss=0;
                float oldcs=0;
                for(int i=0;i<1000000;i++){
                    oldcs = qcos;
                    qcos = coscf * oldcs + sincf * qsin;
                    qsin = coscf * qsin - sincf * oldcs;
                    ss+=qsin;
                    }
                tmp[gid]=ss;
                }
            };
            kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
            }
        tt = System.currentTimeMillis();
        kernel.execute(size);
        System.out.println("gpu time = "+(System.currentTimeMillis()-tt));
        System.out.println("conversion time = "+kernel.getConversionTime());
        System.out.println("execution time = "+kernel.getExecutionTime());
        for (int i = 0; i < size; i++) {
            if (tmp[i]!=tmp2[i])
            System.out.printf("%d + %6.2f = %8.2f\n", i, tmp[i], tmp2[i]);
            }
        kernel.dispose();
        }

    @Override
    public void synch() {
        out = tmp2;
        }
    @Override
    public String getTypeName() {
        return "Тест GPUSin";
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
