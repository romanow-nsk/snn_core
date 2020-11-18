/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

// Базовый интегральный слой с массивом значений - выходов

import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_TypeName;
import romanow.snn_simulator.I_Neuron;
import romanow.snn_simulator.I_NeuronLayer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_ObjectName;
import romanow.snn_simulator.fft.FFTParams;

abstract public class NL_Integrate implements I_NeuronLayer,I_TypeName,I_ObjectName{
    class LayerAdapter implements I_NeuronOutput{
        @Override
        public float getSpike() {
            return 0;
            }
        @Override
            public boolean hasValue(){
            GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
            return false;
            }
        @Override
        public boolean getFire(){
            GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
            return false;
            }
        @Override
        public int getUID() {
            GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
            return 0;
            }
        @Override
        public void setUID(int uid) {
            GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
            }
        }
    protected FFTParams pars = null;
    private String name="";
    protected int subToneCount=0;
    protected I_Layer src=null;
    protected I_NeuronOutput adapters[]=null;    // Объекты-адаптеры
    protected float out[]=null;                // ВЫХОДЫ СЛОЯ
    protected float[] create(){
        float out[] = new float[src.size()];
        for(int i=0;i<out.length;i++)
            out[i]=0;
        return out;
        }
    @Override
    public I_Neuron findByUID(int uid){   // В интегрированном слое нейронов нет
        return null;
        }
    @Override
    public int numerateUID(int uid){       // Нумеровать нечего
        return uid;
        } 
    public void testData(float data[]){
        for(int i=0;i<data.length;i++)
            if (data[i]!=0){
                System.out.println(""+data.getClass().getSimpleName()+" "+i+" "+data[i]);
                return;
                }                
        }
    public void normalize(float data[]){
        int imax=0;
        for(int i=0;i<data.length;i++)
            if (data[i]>data[imax])
                imax = i;
        float k = data[imax]/GBL.FireON;
        if (k==0)
            return;
        for(int i=0;i<data.length;i++)
            data[i] = (float)(data[i]/k);
        }
    public NL_Integrate(){
        }
    @Override
    public int getSubToneCount() {
        return subToneCount;
        }
    @Override
    public int size() {
        return src.size();
        }
    @Override
    public float getSpike(int idx) throws UniException {
        return out[idx];
        }    
    @Override
    public I_NeuronOutput get(final int idx) throws UniException {
        return adapters[idx];
        }
    @Override
    public float[] getSpikes() throws UniException {
        return out;
        }
    @Override
    public String getName() {
        return getTypeName();
        }    
    @Override
    public void addSynapse(I_Layer src) throws UniException {
        this.src = src;
        reset(pars);
        }
    @Override
    public void addSynapseShifted(int offset, I_Layer src) throws UniException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
        }
    @Override
    public void createLayer(I_Neuron proto, int size) throws Exception {
        subToneCount = size/FFT.Octaves/12;
        adapters = new I_NeuronOutput[size];
        for(int i=0;i<adapters.length;i++){
            final int ii = i;
            adapters[i] = new LayerAdapter(){
                @Override
                public float getSpike() {
                    return out[ii];
                    }
                };
            }
        }
    @Override
    public void reset(FFTParams pars) throws UniException {
        this.pars = pars;
        out = create();
        }
    @Override
    public void addInputsLinear(int size, I_Layer src) throws UniException {
        GBL.notSupport(); //To change body of generated methods, choose Tools | Templates.
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
    public void setLearningMode(boolean learinig) {
        }
}
