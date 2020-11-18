/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.gammatone;

// Оригинал http://staffwww.dcs.shef.ac.uk/people/N.Ma/resources/gammatone/
public class GTFilterOriginal {
/*    
%
%  [bm, env, instp, instf] = gammatone_c(x, fs, cf, hrect) 
%
%  x     - input signal
%  fs    - sampling frequency (Hz)
%  cf    - centre frequency of the filter (Hz)
%  hrect - half-wave rectifying if hrect = 1 (default 0)
%
%  bm    - basilar membrane displacement - отклонение базилярной мембраны
%  env   - instantaneous envelope - мгновенная огибающая
%  instp - instantaneous phase (unwrapped radian) - мгновенная фаза
%  instf - instantaneous frequency (Hz) - мгновенная частота
%
%
%  The gammatone filter is commonly used in models of the auditory system.
%  The algorithm is based on Martin Cooke's Ph.D work (Cooke, 1993) using 
%  the base-band impulse invariant transformation. This implementation is 
%  highly efficient in that a mathematical rearrangement is used to 
%  significantly reduce the cost of computing complex exponentials. For 
%  more detail on this implementation see
%  http://www.dcs.shef.ac.uk/~ning/resources/gammatone/
%
%  Once compiled in Matlab this C function can be used as a standard 
%  Matlab function:
%  >> mex gammatone_c.c
%  >> bm = gammatone_c(x, 16000, 200);
%
%  Ning Ma, University of Sheffield
%  n.ma@dcs.shef.ac.uk, 09 Mar 2006
% 
*/

//#define IN_x        prhs[0]
//#define IN_fs       prhs[1]
//#define IN_cf       prhs[2]
//#define IN_hrect    prhs[3]
//#define OUT_bm      plhs[0] /* Basilar membrane response */
//#define OUT_env     plhs[1] /* Instantaneous Hilbert envelope */
//#define OUT_instp   plhs[2] /* Instantaneous phase */
//#define OUT_instf   plhs[3] /* Instantaneous frequency */

    //private static double erb(double x){
    //    return 24.7 * ( 4.37e-3 * ( x ) + 1.0 );
    //    }
    //int nlhs, mxArray* plhs[], int nrhs, const mxArray* prhs[] ){

    final static double BW_CORRECTION=1.0190;
    final static double VERY_SMALL_NUMBER =1e-200;
    public static void filter( 
        double x[], int fs, double cf, boolean hrect,               // Входные параметры
        double bm[], double env[],double  instp[],double  instf[]){ // Выходные параметры
        int i, j, t, nsamples;
        double a, tpt, tptbw, gain;
        double p0r, p1r, p2r, p3r, p4r, p0i, p1i, p2i, p3i, p4i;
        double a1, a2, a3, a4, a5, u0r, u0i; /*, u1r, u1i;*/
        double qcos, qsin, oldcs, coscf, sincf, oldphase, dp, dps;
        nsamples = x.length;
        /*=========================================
        * Initialising variables
        *=========================================
        */
        oldphase = 0.0;
        tpt = ( Math.PI + Math.PI ) / fs;
        double erb = 24.7 * ( 4.37e-3 * ( cf ) + 1.0 );
        tptbw = tpt * erb  * BW_CORRECTION;
        a = Math.exp (-tptbw );
        
        /* based on integral of impulse response */
        gain = ( tptbw*tptbw*tptbw*tptbw ) / 3;

        /* Update filter coefficients */
        a1 = 4.0*a; a2 = -6.0*a*a; a3 = 4.0*a*a*a; a4 = -a*a*a*a; a5 = a*a;
        p0r = 0.0; p1r = 0.0; p2r = 0.0; p3r = 0.0; p4r = 0.0;
        p0i = 0.0; p1i = 0.0; p2i = 0.0; p3i = 0.0; p4i = 0.0;
 
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
        for ( t=0; t<nsamples; t++ ){
            /* Filter part 1 & shift down to d.c. */
            p0r = qcos*x[t] + a1*p1r + a2*p2r + a3*p3r + a4*p4r;
            p0i = qsin*x[t] + a1*p1i + a2*p2i + a3*p3i + a4*p4i;
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
            bm[t] = ( u0r * qcos + u0i * qsin ) * gain;
            if ( hrect && bm[t] < 0 ) {
                bm[t] = 0;                          /* half-wave rectifying */
                }
            // Instantaneous Hilbert envelope
            // env = abs(u) * gain;
            if ( env!=null) {
                env[t] = Math.sqrt ( u0r * u0r + u0i * u0i ) * gain;
                }
            /*==========================================
            * Instantaneous phase
            * instp = unwrap(angle(u));
            *==========================================
            */
            if (instp!=null) {
                instp[t] = Math.atan2 ( u0i, u0r );
                /* unwrap it */
                dp = instp[t] - oldphase;
                if ( Math.abs ( dp ) > Math.PI ) {
                    //#define myMod(x,y)     ( ( x ) - ( y ) * floor ( ( x ) / ( y ) ) )
                    //dps = myMod ( dp + Math.PI, 2 * Math.PI) - Math.PI;
                    double xx = dp + Math.PI;
                    double yy = 2 * Math.PI;
                    dps = xx  - yy*Math.floor(xx/yy)- Math.PI;
                    if ( dps == -Math.PI && dp > 0 ) {
                        dps = Math.PI;
                        }
                    instp[t] = instp[t] + dps - dp;
                    }
                oldphase = instp[t];
                }
            /*==========================================
            * Instantaneous frequency
            * instf = cf + [diff(instp) 0]./tpt;
            *==========================================
            */
            if ( instf!=null && t > 0 ) {
                instf[t-1] = cf + ( instp[t] - instp[t-1] ) / tpt;
                }
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
            }
        if ( instf!=null ) {
            instf[nsamples-1] = cf;
            }
        return;
        }
    
}
