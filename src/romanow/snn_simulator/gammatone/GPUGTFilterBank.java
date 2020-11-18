/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.gammatone;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import romanow.snn_simulator.fft.FFTArray;
import static romanow.snn_simulator.gammatone.GTFilter.BW_CORRECTION;
import static romanow.snn_simulator.gammatone.GTFilter.G_SCALE;
import static romanow.snn_simulator.gammatone.GTFilter.VERY_SMALL_NUMBER;


public class GPUGTFilterBank{
    final static float VERY_SMALL_NUMBER =1e-20F;
    public GPUGTFilterBank(){
        }
    public FFTArray []execute(final GTFilter bank[],final float wave[], final int nQuant, final int sz){
        final float coscf[] = new float[bank.length]; 
        final float sincf[] = new float[bank.length]; 
        final float cf[] = new float[bank.length]; 
        final float fs[] = new float[bank.length]; 
        final float aa[] = new float[bank.length]; 
        final boolean hrect[] = new boolean[bank.length]; 
        for(int i=0;i<bank.length;i++){
            cf[i] = (float)bank[i].cf;
            fs[i] = bank[i].fs;
            hrect[i] = bank[i].hrect;
            double tpt = (( Math.PI + Math.PI ) / fs[i]);
            coscf[i] = (float)Math.cos ( tpt * cf[i] );
            sincf[i] = (float)Math.sin ( tpt * cf[i] );            
            float erb = (24.7f * ( 4.37e-3f * ( cf[i] ) + 1.0f ));
            float tptbw = (float)(tpt * erb  * BW_CORRECTION);
            aa[i] = (float)Math.exp (-tptbw );
            }
        final float out[] = new float[sz * bank.length];
        Kernel kernel = new Kernel(){
            @Override
            public void run() {
                int gid = getGlobalId();
                float bm=0, env=0, instp=0, instf=0,instfPrev=0;
                float gain=0;
                float p0r=0, p1r=0, p2r=0, p3r=0, p4r=0, p0i=0, p1i=0, p2i=0, p3i=0, p4i=0;
                float a1=0, a2=0, a3=0, a4=0, a5=0, u0r=0, u0i=0; 
                float qcos=0, qsin=0, oldcs=0, oldphase=0, dp=0, dps=0;
                float maxBmAbs=0;
                float sum2=0;
                float bmPrev1=0,bmPrev2=0,bmLocMax=0;      // Старые значения и лок. максимум
                oldphase = 0.0F; 
                float PI = 3.1415926f;
                float tpt = ( PI + PI ) / fs[gid];
                float erb = (24.7f * ( 4.37e-3f * ( cf[gid] ) + 1.0f ));
                float tptbw = (tpt * erb  * BW_CORRECTION);
                //------ вычислено выше ---------------------------------------
                //double a = Math.exp (-tptbw );
                // based on integral of impulse response 
                gain = ( tptbw*tptbw*tptbw*tptbw ) / 3;
                // Update filter coefficients 
                float a = 0; // aa[gid];
                a1 = (4.0f*a); a2 = (-6.0f*a*a); a3 = (4.0f*a*a*a);
                a4 = (-a*a*a*a); a5 = (a*a);
                p0r = 0.0F; p1r = 0.0F; p2r = 0.0F; p3r = 0.0F; p4r = 0.0F;
                p0i = 0.0F; p1i = 0.0F; p2i = 0.0F; p3i = 0.0F; p4i = 0.0F;              
                qcos = 1; qsin = 0;   
                // t=0 & q = exp(-i*tpt*t*cf)
                int idx=0;
                for(int st=0; st<sz; st++){
                    bmLocMax=0;
                    bmPrev1=0;
                    bmPrev2=0;
                    maxBmAbs=0;
                    sum2=0;
                    float x = 0;
                    for(int nn=0; nn<nQuant; nn++,idx++){
                        //if (idx < wave.length)
                        //    x = wave[idx];
                        //------- Inline-подстановка ВЫЗОВ ФУНКЦИИ НЕ КОМПИЛИРУЕТСЯ
                        // Filter part 1 & shift down to d.c.
                        p0r = qcos*x + a1*p1r + a2*p2r + a3*p3r + a4*p4r;
                        p0i = qsin*x + a1*p1i + a2*p2i + a3*p3i + a4*p4i;
                        // Clip coefficients to stop them from becoming too close to zero 
                        float xx = p0r < 0 ? -p0r : p0r;
                        if (xx < VERY_SMALL_NUMBER)
                            p0r = 0.0F;
                        xx = p0i < 0 ? -p0i : p0i;                        
                        if (xx < VERY_SMALL_NUMBER)
                            p0i = 0.0F;
                        // Filter part 2
                        u0r = p0r + a1*p1r + a5*p2r;
                        u0i = p0i + a1*p1i + a5*p2i;
                        // Update filter results 
                        p4r = p3r; p3r = p2r; p2r = p1r; p1r = p0r;
                        p4i = p3i; p3i = p2i; p2i = p1i; p1i = p0i;
                        bm = ( u0r * qcos + u0i * qsin ) * gain;
                        if ( hrect[gid] && bm < 0 ) {
                            bm = 0;              
                            }
                        //------------------ Пока без корня
                        //env = (float)(Math.sqrt ( u0r * u0r + u0i * u0i ) * gain);
                        env = ( u0r * u0r + u0i * u0i ) * gain;
                        oldcs = qcos;
                        qcos = coscf[gid] * oldcs + sincf[gid] * qsin;
                        qsin = coscf[gid] * qsin - sincf[gid] * oldcs;
                        float zz = bm < 0 ? -bm : bm;
                        if (zz > maxBmAbs){
                            maxBmAbs = zz;
                            }
                        sum2 += bm*bm;
                        //--------------- Локальный максимум -------------------------------
                        if (bmPrev1 > bm && bmPrev1 > bmPrev2)
                            bmLocMax= bmPrev1;
                        bmPrev2 = bmPrev1;
                        bmPrev1 = bm;
                        //===============================================================
                        }
                    out[gid*sz+st] = sum2;
                    }
                }    
            };
        long tt = System.currentTimeMillis();
        kernel.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
        //kernel.put(kk);
        kernel.put(wave);
        //kernel.put(out);
        kernel.execute(Range.create(bank.length));   
        //kernel.get(kk);
        //kernel.get(wave);
        //kernel.get(out);
        FFTArray fin[] = new FFTArray[sz];
        for(int i=0;i<sz;i++){
            fin[i] = new FFTArray(bank.length);
            for(int j=0;j<bank.length;j++)
                fin[i].set(j, (float)(G_SCALE*Math.sqrt(out[i+sz*j])));
            }
        //convertBack(bank,kk);
        System.out.println("Cohleogram gpu time = "+(System.currentTimeMillis()-tt));
        System.out.println("Cohleogram conversion time = "+kernel.getConversionTime());
        System.out.println("Cohleogram execution time = "+kernel.getExecutionTime());
        kernel.dispose();
        System.out.println("Cohleogram info = "+kernel.getProfileInfo());
        return fin;
        }
    
    public void execute(final GTFilter bank[],final float wave[], final int nQuant){
        final double kk[] = convertFore(bank); 
        Kernel kernel = new Kernel(){
            @Override
            public void run() {
                int gid = getGlobalId();
                int base = gid * DataSize;
                int nn=0;
                for(nn=0; nn<nQuant; nn++){
                    double x = wave[nn];
        //------- Inline-подстановка ВЫЗОВ ФУНКЦИИ НЕ КОМПИЛИРУЕТСЯ
        double oldcs;
        kk[base+p0r] = kk[base+qcos]*x + kk[base+a1]*kk[base+p1r] + kk[base+a2]*kk[base+p2r] + kk[base+a3]*kk[base+p3r] + kk[base+a4]*kk[base+p4r];
        kk[base+p0i] = kk[base+qsin]*x + kk[base+a1]*kk[base+p1i] + kk[base+a2]*kk[base+p2i] + kk[base+a3]*kk[base+p3i] + kk[base+a4]*kk[base+p4i];
        //if (kk[base+p0r]!=0)    
        //    System.out.println(""+gid+"/"+nn+" "+x+" "+kk[base+p0r]+" "+kk[base+p0i]);
        if (kk[base+p0r] < VERY_SMALL_NUMBER && -kk[base+p0r] < VERY_SMALL_NUMBER)
            kk[base+p0r] = 0.0F;
        if (kk[base+p0i] < VERY_SMALL_NUMBER && -kk[base+p0i] < VERY_SMALL_NUMBER)
            kk[base+p0i] = 0.0F;
        kk[base+u0r] = kk[base+p0r] + kk[base+a1]*kk[base+p1r] + kk[base+a5]*kk[base+p2r];
        kk[base+u0i] = kk[base+p0i] + kk[base+a1]*kk[base+p1i] + kk[base+a5]*kk[base+p2i];
        kk[base+p4r] = kk[base+p3r]; kk[base+p3r] = kk[base+p2r]; kk[base+p2r] = kk[base+p1r]; kk[base+p1r] = kk[base+p0r];
        kk[base+p4i] = kk[base+p3i]; kk[base+p3i] = kk[base+p2i]; kk[base+p2i] = kk[base+p1i]; kk[base+p1i] = kk[base+p0i];
        kk[base+bm] = ( kk[base+u0r] * kk[base+qcos] + kk[base+u0i] * kk[base+qsin] ) * gain;
        if ( kk[base+hrect]!=0 && kk[base+bm] < 0 ) {
            kk[base+bm] = 0;                        
            }
        //kk[base+env] = Math.sqrt ( kk[base+u0r] * kk[base+u0r] + kk[base+u0i] * kk[base+u0i] ) * kk[base+gain];
        kk[base+env] = (kk[base+u0r] * kk[base+u0r] + kk[base+u0i] * kk[base+u0i] ) * kk[base+gain];
        oldcs = kk[base+qcos];
        kk[base+qcos] = kk[base+coscf] * oldcs + kk[base+sincf] * kk[base+qsin];
        kk[base+qsin] = kk[base+coscf] * kk[base+qsin] - kk[base+sincf] * oldcs;
        double zz = kk[base+bm] >0 ? kk[base+bm] : -kk[base+bm];
        if (zz > kk[base+maxBmAbs]){
            kk[base+maxBmAbs] = zz;
            }
        kk[base+sum2] += kk[base+bm]*kk[base+bm];
        //--------------- Локальный максимум -------------------------------
        if (kk[base+bmPrev1] > kk[base+bm] && kk[base+bmPrev1] > kk[base+bmPrev2])
            kk[base+bmLocMax]= kk[base+bmPrev1];
        kk[base+bmPrev2] = kk[base+bmPrev1];
        kk[base+bmPrev1] = kk[base+bm];
        //------------------------------------------------------------------
                    }
                }    
            };
        long tt = System.currentTimeMillis();
        kernel.setExecutionMode(Kernel.EXECUTION_MODE.CPU);
        //kernel.put(kk);
        //kernel.put(wave);
        kernel.execute(Range.create(bank.length));   
        //kernel.get(kk);
        //kernel.get(wave);
        convertBack(bank,kk);
        System.out.println("Cohleogram gpu time = "+(System.currentTimeMillis()-tt));
        System.out.println("Cohleogram conversion time = "+kernel.getConversionTime());
        System.out.println("Cohleogram execution time = "+kernel.getExecutionTime());
        kernel.dispose();
        System.out.println("Cohleogram info = "+kernel.getProfileInfo());
        }
    //-------------- НЕ БЕРЕТ --------------------------------------------
    public double []convertFore(GTFilter bank[]){
        double out[] = new double[bank.length*DataSize];
        for(int i=0,base=0;i<bank.length;i++,base+=DataSize){
            out[base+p0r] = bank[i].p0r;
            out[base+p1r] = bank[i].p1r;
            out[base+p2r] = bank[i].p2r;
            out[base+p3r] = bank[i].p3r;
            out[base+p4r] = bank[i].p4r;
            out[base+a1] = bank[i].a1;
            out[base+a2] = bank[i].a2;
            out[base+a3] = bank[i].a3;
            out[base+a4] = bank[i].a4;
            out[base+a5] = bank[i].a5;
            out[base+p0i] = bank[i].p0i;
            out[base+p1i] = bank[i].p1i;
            out[base+p2i] = bank[i].p2i;
            out[base+p3i] = bank[i].p3i;
            out[base+p4i] = bank[i].p4i;
            out[base+u0r] = bank[i].u0r;
            out[base+u0i] = bank[i].u0i;
            out[base+qcos] = bank[i].qcos;
            out[base+qsin] = bank[i].qsin;
            out[base+coscf] = bank[i].coscf;
            out[base+sincf] = bank[i].sincf;
            out[base+bm] = bank[i].bm;
            out[base+bmPrev1] = bank[i].bmPrev1;
            out[base+bmPrev2] = bank[i].bmPrev2;
            out[base+bmLocMax] = bank[i].bmLocMax;
            out[base+gain] = bank[i].gain;
            out[base+env] = bank[i].env;
            out[base+sum2] = bank[i].sum2;
            out[base+maxBmAbs] = bank[i].maxBmAbs;
            out[base+hrect] = bank[i].hrect ? 1 : 0;
            }
        return out;
        }
    public void convertBack(GTFilter bank[], double out[]){
        for(int i=0,base=0;i<bank.length;i++,base+=DataSize){
            bank[i].p0r = out[base+p0r];
            bank[i].p1r = out[base+p1r];
            bank[i].p2r = out[base+p2r];
            bank[i].p3r = out[base+p3r];
            bank[i].p4r = out[base+p4r];
            bank[i].p0i = out[base+p0i];
            bank[i].p1i = out[base+p1i];
            bank[i].p2i = out[base+p2i];
            bank[i].p3i = out[base+p3i];
            bank[i].p4i = out[base+p4i];
            bank[i].u0r = out[base+u0r];
            bank[i].u0i = out[base+u0i];
            bank[i].qcos = out[base+qcos];
            bank[i].qsin = out[base+qsin];
            bank[i].bm = out[base+bm];
            bank[i].bmPrev1 = out[base+bmPrev1];
            bank[i].bmPrev2 = out[base+bmPrev2];
            bank[i].bmLocMax = out[base+bmLocMax];
            bank[i].env = out[base+env];
            bank[i].sum2 = out[base+sum2];
            bank[i].maxBmAbs = out[base+maxBmAbs];
            }
        }
    //-------------- Вычисление через массив параметров
    private final static int DataSize=30;
    private final static int p0r=0;
    private final static int p1r=1;
    private final static int p2r=2;
    private final static int p3r=3;
    private final static int p4r=4;
    private final static int p0i=5;
    private final static int p1i=6;
    private final static int p2i=7;
    private final static int p3i=8;
    private final static int p4i=9;
    private final static int u0r=10;
    private final static int u0i=11;
    private final static int a1=12;
    private final static int a2=13;
    private final static int a3=14;
    private final static int a4=15;
    private final static int a5=16;
    private final static int qcos=17;
    private final static int qsin=18;
    private final static int coscf=19;
    private final static int sincf=20;
    private final static int bm=21;
    private final static int bmPrev1=22;
    private final static int bmPrev2=23;
    private final static int bmLocMax=24;
    private final static int gain=25;
    private final static int env=26;
    private final static int sum2=27;
    private final static int maxBmAbs=28;
    private final static int hrect=29;
    public static double filterOne(double x, int base, double kk[]){
        /*
        double oldcs;
        kk[base+p0r] = kk[base+qcos]*x + a1*kk[base+p1r] + kk[base+a2]*kk[base+p2r] + kk[base+a3]*kk[base+p3r] + kk[base+a4]*kk[base+p4r];
        kk[base+p0i] = kk[base+qsin]*x + kk[base+a1]*kk[base+p1i] + kk[base+a2]*kk[base+p2i] + kk[base+a3]*kk[base+p3i] + kk[base+a4]*kk[base+p4i];
        //if (Math.abs(kk[base+p0r]) < VERY_SMALL_NUMBER)
        //    kk[base+p0r] = 0.0F;
        //if (Math.abs(kk[base+p0i]) < VERY_SMALL_NUMBER)
        //    kk[base+p0i] = 0.0F;
        kk[base+u0r] = kk[base+p0r] + kk[base+a1]*kk[base+p1r] + kk[base+a5]*kk[base+p2r];
        kk[base+u0i] = kk[base+p0i] + kk[base+a1]*kk[base+p1i] + kk[base+a5]*kk[base+p2i];
        kk[base+p4r] = kk[base+p3r]; kk[base+p3r] = kk[base+p2r]; kk[base+p2r] = kk[base+p1r]; kk[base+p1r] = kk[base+p0r];
        kk[base+p4i] = kk[base+p3i]; kk[base+p3i] = kk[base+p2i]; kk[base+p2i] = kk[base+p1i]; kk[base+p1i] = kk[base+p0i];
        kk[base+bm] = ( kk[base+u0r] * kk[base+qcos] + kk[base+u0i] * kk[base+qsin] ) * gain;
        if ( kk[base+hrect]!=0 && kk[base+bm] < 0 ) {
            kk[base+bm] = 0;                        
            }
        //kk[base+env] = Math.sqrt ( kk[base+u0r] * kk[base+u0r] + kk[base+u0i] * kk[base+u0i] ) * kk[base+gain];
        kk[base+env] = (kk[base+u0r] * kk[base+u0r] + kk[base+u0i] * kk[base+u0i] ) * kk[base+gain];
        oldcs = kk[base+qcos];
        kk[base+qcos] = kk[base+coscf] * oldcs + kk[base+sincf] * kk[base+qsin];
        kk[base+qsin] = kk[base+coscf] * kk[base+qsin] - kk[base+sincf] * oldcs;
        double zz = kk[base+bm] >0 ? kk[base+bm] : -kk[base+bm];
        if (zz > kk[base+maxBmAbs]){
            kk[base+maxBmAbs] = zz;
            }
        kk[base+sum2] += kk[base+bm]*kk[base+bm];
        //--------------- Локальный максимум -------------------------------
        if (kk[base+bmPrev1] > kk[base+bm] && kk[base+bmPrev1] > kk[base+bmPrev2])
            kk[base+bmLocMax]= kk[base+bmPrev1];
        kk[base+bmPrev2] = kk[base+bmPrev1];
        kk[base+bmPrev1] = kk[base+bm];
        //------------------------------------------------------------------
        */
        return kk[base+bm];
        }

}
    
