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
    private boolean FFTWindowReduce=true;  // Укорочение интервалов для высоких частот по октавам
    private int W=1024;                    // Ширина окна (отсчетов)
    private int procOver=75;               // Процент перекрытия двух соседних окон
    private boolean logFreqMode=true;      // Октавный режим (true)
    private int subToneCount=2;            // Частот в полутоне
    private double  F_SCALE=3.0;           // Коэффициент перемножения спектр*гамматон
    private boolean p_Cohleogram=false;    // Наличие кохлеограммы
    private boolean p_GPU=false;           // GPU включен
    private int winMode=FFT.WinModeRectangle; // Вид функции окна
    private float compressGrade=0;         // Степень компрессии
    private boolean compressMode=false;
    private float kAmpl=1;                 // Ампл. компрессии
    public FFTParams(int W, int procOver, boolean logFreqMode,
        int subToneCount, boolean p_Cohleogram, boolean p_GPU, 
        boolean FFTWindowReduce,int GPUmode, double f_SCALE){
        this.GPUmode = GPUmode;
        this.FFTWindowReduce = FFTWindowReduce;
        this.W = W;
        this.procOver = procOver;
        this.logFreqMode= logFreqMode;
        this.subToneCount = subToneCount;
        this.p_Cohleogram = p_Cohleogram;
        this.p_GPU = p_GPU;
        F_SCALE = f_SCALE;
        }
    public FFTParams procOver(int procOver0){
        procOver = procOver0;
        return this;
        }
    public FFTParams W(int W0){
        W = W0;
        return this;
        }
    public FFTParams logFreqMode(boolean logFreqMode0){
        logFreqMode = logFreqMode0;
        return this;
        }
    public FFTParams subToneCount(int subToneCount0){
        subToneCount = subToneCount0;
        return this;
        }
    public FFTParams p_Cohleogram(boolean p_Cohleogram0){
        p_Cohleogram = p_Cohleogram0;
        return this;
        }
    public FFTParams FFTWindowReduce(boolean FFTWindowReduce0){
        FFTWindowReduce = FFTWindowReduce0;
        return this;
        }
    public FFTParams p_GPU(boolean p_GPU0){
        p_GPU = p_GPU0;
        return this;
        }
    public FFTParams compressMode(boolean compressMode0){
        compressMode = compressMode0;
        return this;
        }
    public FFTParams compressGrade(float compressGrade0){
        compressGrade = compressGrade0;
        return this;
        }
    public FFTParams f_SCALE(double f_SCALE0){
        F_SCALE = f_SCALE0;
        return this;
        }
    public FFTParams kAmpl(float kAmpl0){
        kAmpl = kAmpl0;
        return this;
        }
    public FFTParams winMode(int winMode0){
        winMode = winMode0;
        return this;
        }
    public FFTParams GPUMode(int GPUMode0){
        GPUmode = GPUMode0;
        return this;
        }
    public float kAmpl(){ return kAmpl; }
    public int winMode(){
        return winMode;
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
                "\nНаличие кохлеограммы="+p_Cohleogram+"\nКомпрессия "+(compressMode ? ("+\nУровень="+compressGrade+"\nАмплитуда="+kAmpl) : "-");
        }
    public float compressGrade() {
        return compressGrade; }
    public boolean compressMode() {
        return compressMode; }
    public double F_SCALE() {
        return F_SCALE;
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
        F_SCALE=in.readDouble();
        winMode=in.readInt();
        compressMode=in.readBoolean();
        compressGrade=in.readFloat();
        kAmpl=in.readFloat();
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
        out.writeDouble(F_SCALE);
        out.writeInt(winMode);
        out.writeBoolean(compressMode);
        out.writeFloat(compressGrade);
        out.writeFloat(kAmpl);
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
