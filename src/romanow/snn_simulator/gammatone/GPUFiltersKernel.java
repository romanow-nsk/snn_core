/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.gammatone;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import romanow.snn_simulator.fft.FFTArray;
import static romanow.snn_simulator.gammatone.GPUGTFilterBank.VERY_SMALL_NUMBER;
import static romanow.snn_simulator.gammatone.GTFilter.BW_CORRECTION;
import static romanow.snn_simulator.gammatone.GTFilter.G_SCALE;

/**
 *
 * @author romanow
 */
// Для фоновой обработки всего потока
public class GPUFiltersKernel extends Kernel{
    private float coscf[];      // Начальные синусы
    private float sincf[];      // Начальные косинусы
    private float cf[];         // Собственная частота
    private float fs[];         // Частота дискретизации 44100
    private float aa[];         // Промежуточный паарметр фильтра - инициализация
    private boolean hrect[];
    private float wave[];       // Сэмплы входного сигнала
    private float out[];        // Двумерый массив кохлеограммы в линейном
    private int nQuant;         // Шаг моделирования в сэмплах
    private int sz;
    private int wsz;
    public final static int buffDepth=500;        // Глубина буферизации
    private int stBeg=0;
    private int stFin=0;
    public FFTArray []execute(GTFilter bank[],float wave0[], int nQuant, int sz,int GPUmode){
        this.wave = (float[])wave0.clone();
        wsz = wave0.length;
        for(int i=0;i<wave0.length;i++)
            wave[i] = wave0[i];
        this.nQuant = nQuant;
        this.sz = sz;
        coscf = new float[bank.length]; 
        sincf = new float[bank.length]; 
        cf = new float[bank.length]; 
        fs = new float[bank.length]; 
        aa = new float[bank.length]; 
        hrect = new boolean[bank.length]; 
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
        out = new float[sz * bank.length];
        long tt = System.currentTimeMillis();
        if (GPUmode==0)
            setExecutionMode(Kernel.EXECUTION_MODE.GPU);
        if (GPUmode==1)
            setExecutionMode(Kernel.EXECUTION_MODE.CPU);
        if (GPUmode==2)
            setExecutionMode(Kernel.EXECUTION_MODE.JTP);
        execute(Range.create(bank.length));   
        System.out.println("Cohleogram gpu time = "+(System.currentTimeMillis()-tt));
        System.out.println("Cohleogram conversion time = "+getConversionTime());
        System.out.println("Cohleogram execution time = "+getExecutionTime());
        System.out.println("Cohleogram info = "+getProfileInfo());
        dispose();
        FFTArray fin[] = new FFTArray[sz];
        int kk=0;
        for(int i=0;i<sz;i++){
            fin[i] = new FFTArray(bank.length);
            for(int j=0;j<bank.length;j++,kk++)
                fin[i].set(j, (float)(G_SCALE*Math.sqrt(out[kk])));
            }
        return fin;
        }

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
                float a =  aa[gid];
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
                        if (idx < wsz)
                            x = wave[idx];
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
    }
