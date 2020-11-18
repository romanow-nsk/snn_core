/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import java.util.Vector;
import romanow.snn_simulator.UniException;

/**
 *
 * @author romanow
 */
public class GBL {
    public static int MesTrass = 0;
    public static int MesInfo = 1;
    public static int MesWarning = 2;
    public static int MesError = 3;
    public static int MesFatal = 4;
    public static String NeuronLayerName="Слой нейронов";
    public static String LoadedModel = "Загруженная";
    //----------- Синглетон контекста ------------------------------------------
    private static GBL context = null;
    public static GBL gbl(){
        if (context == null)
            context = new GBL();
        return context;
        }
    public static void notSupport(){
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String message = "";
        StackTraceElement element = stackTraceElements[2];
        String className = element.getClassName();
        int idx=className.lastIndexOf(".");
        if (idx!=-1) className = className.substring(idx+1);
        String methodName = element.getMethodName();
        String from="";
        if (stackTraceElements.length>3){
            element = stackTraceElements[3];
            className = element.getClassName();
            idx=className.lastIndexOf(".");
            if (idx!=-1) className = className.substring(idx+1);
            int num = element.getLineNumber();
            from = " -> "+className + "["+num+"]";
            }
        throw new UnsupportedOperationException("Не поддерживается "+className + ":" + methodName+from);
        }
    //--------------------------------------------------------------------------
    public static float FireON=1.0F;
    public static float FireOFF=0;
    public static float FireOFFLevel=0.2F;
    public static float FireONLevel=0.8F;
    public static float FireMiddle=0.5F;
    // При выходе за границы - берутся граничные
    public static int addInputsLinear(Vector<I_NeuronOutput> dst,int idx, int size, I_Layer src) throws UniException{
        int count = 0;
        idx -= size/2;
        while(size!=0){
            int i=idx;
            if (i<0)
                i=0;
            if (i>=src.size())
                i=src.size()-1;
            dst.add(src.get(i));
            idx++;
            size--;
            count++;
            }
        return count;
        }
    public static int addInputsOctave(Vector<I_NeuronOutput> dst,int idx, int octaveStep, int size, I_Layer src) throws UniException{
        int count=0;
        while(size!=0 && idx < src.size()){
            dst.add(src.get(idx));
            idx += octaveStep;
            size--;
            count++;
            }
        return count;
        }
    public static float []extend(float in[], float val){
        float out[] = new float[in.length+1];
        for(int i=0;i<in.length;i++)
            out[i] = in[i];
        out[out.length-1] = val;
        return out;
        }
    public static float []extend(float in[], int dim, float value){
        float out[] = new float[in.length+dim];
        for(int i=0;i<in.length;i++)
            out[i] = in[i];
        for(int i=in.length;i<out.length;i++)
            out[i] = value;
        return out;
        }
    public static String afterP(double val, int nn){
        String ss=""+val;
        int idx=ss.indexOf(".");
        if (idx==-1)
            return ss;
        if (idx+nn+1 >= ss.length())
            return ss;
        return ss.substring(0,idx+nn+1);
        }    
    public static void test() throws Exception{
        notSupport();
        }
    public static void main(String argv[]) throws Exception{
        test();
        }
}
