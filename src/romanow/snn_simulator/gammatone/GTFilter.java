/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.gammatone;

// Оригинал http://staffwww.dcs.shef.ac.uk/people/N.Ma/resources/gammatone/
public class GTFilter {
    final static float BW_CORRECTION=1.0190F;
    final static float VERY_SMALL_NUMBER =1e-40F; 
    final static float G_SCALE=1;       // Масштаб кохлеограммы    
//-------------------------- В объекте вычисляется след. значение --------------
    int fs;                                     // Частота дискретизации 44100
    double cf;                                  // Собственная чатота  фильтра
    double bm, env, instp, instf,instfPrev=0;   // bm - текущая амплитуда мембраны
    double gain;
    double p0r, p1r, p2r, p3r, p4r, p0i, p1i, p2i, p3i, p4i;
    double a1, a2, a3, a4, a5, u0r, u0i; /*, u1r, u1i;*/
    double qcos, qsin, oldcs, coscf, sincf, oldphase, dp, dps;
    boolean hrect;
    double maxBmAbs=0;
    double sum2=0;
    double bmPrev1,bmPrev2,bmLocMax;      // Старые значения и лок. максимум
    public float getBm(){
        return (float)(G_SCALE*bm);
        }
    public float getEnv(){
        return (float)(G_SCALE*env);
        }
    public float getMid(){
        return (float)(G_SCALE*Math.sqrt(sum2));
        }
    public float getBmMax(){
        return (float)(G_SCALE*maxBmAbs);
        }
    public float getBmLocMax(){
        return (float)(G_SCALE*bmLocMax);
        }
    public void clearMid(){
        sum2=0;
        }
    public void clearBmMax(){
        maxBmAbs=0;
        }
    public void clearBmLocBax(){
        bmLocMax=0;
        bmPrev1=bmPrev2=0;
        }
    public GTFilter(int fs, float cf){
        this(fs,cf,false);
        }
    public GTFilter(int fs, float cf, boolean hrect){
        this.fs = fs;
        this.cf = cf;
        this.hrect = hrect;
        sum2=0;
        bmLocMax=0;
        bmPrev1=0;
        bmPrev2=0;        
        /*=========================================
        * Initialising variables
        *=========================================
        */
        int i, j, t, nsamples;
        oldphase = 0.0F;
        double tpt = ( Math.PI + Math.PI ) / fs;
        double erb = (24.7 * ( 4.37e-3 * ( cf ) + 1.0 ));
        double tptbw = tpt * erb  * BW_CORRECTION;
        double a = Math.exp (-tptbw );
        /* based on integral of impulse response */
        gain = ( tptbw*tptbw*tptbw*tptbw ) / 3;
        /* Update filter coefficients */
        a1 = (4.0*a); a2 = (-6.0*a*a); a3 = (4.0*a*a*a);
        a4 = (-a*a*a*a); a5 = (a*a);
        p0r = 0.0F; p1r = 0.0F; p2r = 0.0F; p3r = 0.0F; p4r = 0.0F;
        p0i = 0.0F; p1i = 0.0F; p2i = 0.0F; p3i = 0.0F; p4i = 0.0F;
        /*===========================================================
        * exp(a+i*b) = exp(a)*(cos(b)+i*sin(b))
        * q = exp(-i*tpt*cf*t) = cos(tpt*cf*t) + i*(-sin(tpt*cf*t))
        * qcos = cos(tpt*cf*t)
        * qsin = -sin(tpt*cf*t)
        *===========================================================
        */
        coscf = Math.cos ( tpt * cf );
        sincf = Math.sin ( tpt * cf );
        qcos = 1; qsin = 0;   /* t=0 & q = exp(-i*tpt*t*cf)*/
        }    
    public float filterOne(float x){ 
            /* Filter part 1 & shift down to d.c. */
            p0r = qcos*x + a1*p1r + a2*p2r + a3*p3r + a4*p4r;
            p0i = qsin*x + a1*p1i + a2*p2i + a3*p3i + a4*p4i;
            /* Clip coefficients to stop them from becoming too close to zero */
            if (Math.abs(p0r) < VERY_SMALL_NUMBER)
                p0r = 0.0F;
            if (Math.abs(p0i) < VERY_SMALL_NUMBER)
                p0i = 0.0F;
            /* Filter part 2 */
            u0r = p0r + a1*p1r + a5*p2r;
            u0i = p0i + a1*p1i + a5*p2i;
            /* Update filter results */
            p4r = p3r; p3r = p2r; p2r = p1r; p1r = p0r;
            p4i = p3i; p3i = p2i; p2i = p1i; p1i = p0i;
            // Basilar membrane response
            // 1/ shift up in frequency first: (u0r+i*u0i) * exp(i*tpt*cf*t) = (u0r+i*u0i) * (qcos + i*(-qsin))
            // 2/ take the real part only: bm = real(exp(j*wcf*kT).*u) * gain;
            bm = ( u0r * qcos + u0i * qsin ) * gain;
            if ( hrect && bm < 0 ) {
                bm = 0;                          /* half-wave rectifying */
                }
            //--------------- Пока вычисление дополнительных параметров --------
            /* Instantaneous Hilbert envelope */
            //  env = abs(u) * gain;
            env = Math.sqrt ( u0r * u0r + u0i * u0i ) * gain;
            /*==========================================
            * Instantaneous phase
            * instp = unwrap(angle(u));
            *==========================================
            */
            //instp = Math.atan2 ( u0i, u0r );
            /* unwrap it */
            //dp = instp - oldphase;
            /* #define myMod(x,y)     ( ( x ) - ( y ) * floor ( ( x ) / ( y ) ) )
                dps = myMod ( dp + Math.PI, 2 * Math.PI) - Math.PI; 
            */
            //if ( Math.abs ( dp ) > Math.PI ) {
            //    float xx = dp + Math.PI;
            //    float yy = 2 * Math.PI;
            //    dps = xx  - yy*Math.floor(xx/yy)- Math.PI;
            //    if ( dps == -Math.PI && dp > 0 ) {
            //        dps = Math.PI;
            //        }
            //    instp = instp + dps - dp;
            //    }
            //oldphase = instp;
            /*==========================================
            * Instantaneous frequency
            * instf = cf + [diff(instp) 0]./tpt;
            *==========================================
            */
            //float xx = cf + ( instf - instfPrev ) / tpt;
            //instfPrev = instf;
            //instf = xx;
            /*====================================================
            * The basic idea of saving computational load:
            * cos(a+b) = cos(a)*cos(b) - sin(a)*sin(b)
            * sin(a+b) = sin(a)*cos(b) + cos(a)*sin(b)
            * qcos = cos(tpt*cf*t) = cos(tpt*cf + tpt*cf*(t-1))
            * qsin = -sin(tpt*cf*t) = -sin(tpt*cf + tpt*cf*(t-1))
            *====================================================
            */
            qcos = coscf * ( oldcs = qcos ) + sincf * qsin;
            qsin = coscf * qsin - sincf * oldcs;
            double zz = Math.abs(bm);
            if (zz > maxBmAbs){
                maxBmAbs = zz;
                }
            sum2 += bm*bm;
            //--------------- Локальный максимум -------------------------------
            if (bmPrev1 > bm && bmPrev1 > bmPrev2)
                bmLocMax= bmPrev1;
            bmPrev2 = bmPrev1;
            bmPrev1 = bm;
            //------------------------------------------------------------------
            return (float)(G_SCALE*bm);
            }
}
