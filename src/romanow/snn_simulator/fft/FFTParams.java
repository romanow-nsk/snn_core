/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

/**
 *
 * @author romanow
 */
public class FFTParams {
    final public int GPUmode;
    final public boolean FFTWindowReduce;
    final public int W;
    final public int procOver;
    public boolean logFreqMode;
    final public int subToneCount;
    final public boolean p_Cohleogram;
    final public boolean p_GPU;
public FFTParams(int W, int procOver, boolean logFreqMode, 
        int subToneCount, boolean p_Cohleogram, boolean p_GPU, 
        boolean FFTWindowReduce,int GPUmode){
        this.GPUmode = GPUmode;
        this.FFTWindowReduce = FFTWindowReduce;
        this.W = W;
        this.procOver = procOver;
        this.logFreqMode= logFreqMode;
        this.subToneCount = subToneCount;
        this.p_Cohleogram = p_Cohleogram;
        this.p_GPU = p_GPU;
        }    
}
