/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

// Сбор статистики по слою

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_ObjectName;
import romanow.snn_simulator.I_TextStream;
import romanow.snn_simulator.I_TypeName;
import romanow.snn_simulator.Token;
import romanow.snn_simulator.UniException;

public class LayerStatistic implements I_TypeName,I_ObjectName,I_TextStream{
    private String name="";
    private int count=0;
    private int size=0;
    private I_Layer src=null;
    private float prev[]=null;
    private float sumT[]=null;          // Сумма по времени
    private float sum2T[]=null;         // Сумма квадратов по времени
    private float sum2DiffF[]=null;     // Корреляция по частоте
    private float sum2DiffT[]=null;     // Корреляция по времени
    public void reset(){
        prev = null;
        size = src.size();
        sumT=new float[size];
        sum2T=new float[size];
        sum2DiffF=new float[size];
        sum2DiffT=new float[size];
        for(int i=0;i<size;i++){
            sumT[i]=0;
            sum2T[i]=0;
            sum2DiffT[i]=0;
            sum2DiffF[i]=0;
            }
        }
    public LayerStatistic(I_Layer src, String name){
        this.src = src;
        setObjectName(name);
        reset();
        }
    public void addStatistic() throws UniException{
        if (src.getSpikes()==null){
            System.out.println("Нет данных для статистики "+this.getObjectName());
            return;
            }
        float data[] = src.getSpikes().clone();
        for(int i=0;i<size;i++){
            sumT[i]+=data[i];
            sum2T[i]+=data[i]*data[i];
            if (prev!=null)
                sum2DiffT[i]+=(data[i]-prev[i])*(data[i]-prev[i]);
            if (i!=0 && i!=size-1){
                sum2DiffF[i]+=(data[i]-data[i-1])*(data[i]-data[i-1]);
                sum2DiffF[i]+=(data[i]-data[i+1])*(data[i]-data[i+1]);
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
        return getMid(getDisps(sum2T));
        }
    public float[] getMids(){
        float out[] = sumT.clone();
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
        return getDisps(sum2T);
        }
    public float[] getDiffsF(){
        return getDisps(sum2DiffF);
        }
    public float getDiffF(){
        return getMid(getDiffsF());
        }
    public float[] getDiffsT(){
        return getDisps(sum2DiffT);
        }
    public float getDiffT(){
        return getMid(getDiffsT());
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

    
}
