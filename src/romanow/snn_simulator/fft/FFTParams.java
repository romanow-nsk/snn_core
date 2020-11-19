/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author romanow
 */
public class FFTParams implements FFTBinStream{
    private int GPUmode;                   // Вид режима GPU
    private boolean FFTWindowReduce;       // Укорочение интервалов для высоких частот по октавам
    private int W;                         // Ширина окна (отсчетов)
    private int procOver;                  // Процент перекрытия двух соседних окон
    private boolean logFreqMode;           // Октавный режим (true)
    private int subToneCount;              // Частот в полутоне
    private boolean p_Cohleogram;          // Наличие кохлеограммы
    private boolean p_GPU;                 // GPU включен
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
    public FFTParams(int W, int procOver, int subToneCount, boolean FFTWindowReduce){
        GPUmode = 0;
        this.FFTWindowReduce = FFTWindowReduce;
        this.W = W;
        this.procOver = procOver;
        this.logFreqMode= true;
        this.subToneCount = subToneCount;
        this.p_Cohleogram = true;
        p_GPU = false;
    }
    public String toString(){
        return "Укорочение интервалов ВЧ="+FFTWindowReduce+"\nШирина окна="+W+
                "\nПроцент перекрытия="+procOver+"\nОктавный режим="+logFreqMode+"\nЧастот в полутоне="+subToneCount+
                "\nНаличие кохлеограммы="+p_Cohleogram;
        }
    public FFTParams(){}
    @Override
    public void load(DataInputStream in, int formatVersion) throws IOException {
        GPUmode=in.readInt();
        FFTWindowReduce=in.readBoolean();
        W=in.readInt();
        procOver=in.readInt();
        logFreqMode=in.readBoolean();
        subToneCount=in.readInt();
        p_Cohleogram=in.readBoolean();
        p_GPU=in.readBoolean();
        }

    @Override
    public void save(DataOutputStream out) throws IOException {
        out.writeInt(GPUmode);
        out.writeBoolean(FFTWindowReduce);
        out.writeInt(W);
        out.writeInt(procOver);
        out.writeBoolean(logFreqMode);
        out.writeInt(subToneCount);
        out.writeBoolean(p_Cohleogram);
        out.writeBoolean(p_GPU);
        }

    public int GPUmode() {
        return GPUmode; }
    public boolean FFTWindowReduce() {
        return FFTWindowReduce; }
    public int W() {
        return W; }
    public int procOver() {
        return procOver; }
    public boolean logFreqMode() {
        return logFreqMode; }
    public int subToneCount() {
        return subToneCount; }
    public boolean p_Cohleogram() {
        return p_Cohleogram; }
    public boolean p_GPU() {
        return p_GPU; }
    public void setLogFreqMode(boolean logFreqMode) {
        this.logFreqMode = logFreqMode; }
}
