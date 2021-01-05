/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

// Сбор статистики по слою

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_ObjectName;
import romanow.snn_simulator.I_TextStream;
import romanow.snn_simulator.I_TypeName;
import romanow.snn_simulator.Token;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.fft.FFT;

public class LayerStatistic implements I_TypeName,I_ObjectName,I_TextStream{
    class SmoothArray{
        float data[];
        SmoothArray(int size){
            data = new float[size];
            for(int i=0;i<size;i++)
                data[i]=0;
            }
        void smoothOne(){
            int size = data.length;
            float out[] = new float[size];
            out[0]=(float)( 0.5*(data[1]+data[0]));
            for(int i=1;i<size-1;i++)
                out[i] = (float)( 0.5*(0.5*(data[i-1]+data[i+1])+data[i]));
            out[size-1]=(float)( 0.5*(data[size-2]+data[size-1]));
            data = out;
            }
        void smooth(int count){
            while (count-->0)
                smoothOne();
            }
        }
    private String name="";
    private int count=0;
    private int size=0;
    private boolean noReset=true;
    private I_Layer src=null;
    private float prev[]=null;
    private SmoothArray sumT=null;          // Сумма по времени
    private SmoothArray sum2T=null;         // Сумма квадратов по времени
    private SmoothArray sum2DiffF=null;     // Корреляция по частоте
    private SmoothArray sum2DiffT=null;     // Корреляция по времени
    public void reset() {
        noReset=true;
        }
    public void lasyReset(float data[]){
        if (!noReset)
            return;
        count=0;
        noReset=false;
        prev = data;
        size = data.length;
        sumT=new SmoothArray(size);
        sum2T=new SmoothArray(size);
        sum2DiffF=new SmoothArray(size);
        sum2DiffT=new SmoothArray(size);
        }
    public void smooth(int steps){
        sumT.smooth(steps);
        sum2T.smooth(steps);
        sum2DiffF.smooth(steps);
        sum2DiffT.smooth(steps);
        }
    public LayerStatistic(I_Layer src, String name){
        this.src = src;
        setObjectName(name);
        reset();
        }
    public LayerStatistic(String name){
        this.src = null;
        setObjectName(name);
        reset();
        }
    public void addStatistic() throws UniException{
        if (src.getSpikes()==null){
            System.out.println("Нет данных для статистики "+this.getObjectName());
            return;
            }
        addStatistic(src.getSpikes());
        }
    public void addStatistic(float src[]) throws UniException{
        float data[] = src.clone();
        lasyReset(data);
        for(int i=0;i<size;i++){
            sumT.data[i]+=data[i];
            sum2T.data[i]+=data[i]*data[i];
            if (prev!=null)
                sum2DiffT.data[i]+=(data[i]-prev[i])*(data[i]-prev[i]);
            if (i!=0 && i!=size-1){
                sum2DiffF.data[i]+=(data[i]-data[i-1])*(data[i]-data[i-1]);
                sum2DiffF.data[i]+=(data[i]-data[i+1])*(data[i]-data[i+1]);
                }
            }
        prev = data;
        count++;
        }
    public int getCount(){
        return count;
        }
    //--------------- Среднее и дисперсия для массивов -------------------------
    private float getMid(float vv[]){
        float res=0;
        for(int i=0;i<size;i++)
            res+=vv[i];
        return res/size;
        }
    public float[] getDisps(float vv[]){
        float out[] = vv.clone();
        for(int i=0;i<size;i++){
            if (count==0)
                out[i]=0;
            else
                out[i] = (float)Math.sqrt(out[i]/count);
            }
        return out;
        }
    //--------------------------------------------------------------------------
    public float getDisp(){
        return getMid(getDisps(sum2T.data));
        }
    public float[] getMids(){
        float out[] = sumT.data.clone();
        for(int i=0;i<size;i++){
            if (count==0)
                out[i]=0;
            else
                out[i]/=count;
            }
        return out;
        }
    public float getMid(){
        return getMid(getMids());
        }
    public float[] getDisps(){
        return getDisps(sum2T.data);
        }
    public float[] getDiffsF(){
        return getDisps(sum2DiffF.data);
        }
    public float getDiffF(){
        return getMid(getDiffsF());
        }
    public float[] getDiffsT(){
        return getDisps(sum2DiffT.data);
        }
    public float getDiffT(){
        return getMid(getDiffsT());
        }
    public ArrayList<Extreme> createExtrems(boolean byLevel, int nFirst, int nLast){
        ArrayList<Extreme> out = new ArrayList<>();
        for(int i=nFirst+1;i<size-1-nLast;i++)
            if (sumT.data[i]>sumT.data[i-1] && sumT.data[i]>sumT.data[i+1]){
                int k1,k2;
                for(k1=i;k1>0 && sumT.data[k1]>sumT.data[k1-1];k1--);
                for(k2=i;k2<sumT.data.length-1 && sumT.data[k2]>sumT.data[k2+1];k2++);
                double d1 = sumT.data[i]-sumT.data[k1];
                double d2 = sumT.data[i]-sumT.data[k2];
                double diff = Math.sqrt(d1*d1+d2*d2);
                out.add(new Extreme(sumT.data[i]/count,(int)((i+1.0)* FFT.sizeHZ/2/size),diff));
                }
        if (byLevel)
            out.sort(new Comparator<Extreme>() {
                @Override
                public int compare(Extreme o1, Extreme o2) {
                    if (o1.value==o2.value) return 0;
                    return o1.value > o2.value ? -1 : 1;
                    }
                });
        else
            out.sort(new Comparator<Extreme>() {
                @Override
                public int compare(Extreme o1, Extreme o2) {
                    if (o1.diff==o2.diff) return 0;
                    return o1.diff > o2.diff ? -1 : 1;
                }
            });
        return out;
        }
    //--------------------------------------------------------------------------
    @Override
    public String getTypeName() {
        return "Статистика";
        }
    @Override
    public String getName() {
        return getObjectName();
        }    
    @Override
    public String getObjectName() {
        return name;
        }
    @Override
    public void setObjectName(String name) {
        this.name = name;
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
    //------------------- Коррекция экспоненты--------------------------
    public double correctExp(int nPoints){
        float a0=sumT.data[0];
        double k=0;
        for(int i=0;i<nPoints;i++)
            k += -Math.log(sumT.data[i+1]/sumT.data[i]);
        k /=nPoints;
        for(int i=0;i<sumT.data.length;i++)
            sumT.data[i]-=a0*Math.exp(-k*i);
        return k;
        }
    //-----------------------------------------------------------------

    
}
