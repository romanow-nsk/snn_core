/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

// Базовый интегральный слой с массивом значений - выходов

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_Neuron;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.Token;
import romanow.snn_simulator.fft.FFTParams;

public abstract class NL_IntegrateBuffered extends NL_Integrate{
    protected int depth=50;                     // Глубина буферизации
    private int stepIdx=0;                      // Текущий индекс в буфере
    private boolean full=false;                 // Первоначальное заполнение буфера
    protected float inData[][]=null;            // Буферизованные данные 
    protected float outData[][]=null;           // Буферизованные данные 
    private float emptyData[]=null;             // Пустые данные (для незаполненного FIFO)
    /* Когда наполняет буфер входными векторами, устанавливает stepIdx=0
    и вызывает multiStep, после чего stepIdx - индекс очередного выходного массива.
    step добавляет входной вектор, после чего сдвишает stepIdx и выходной берется
    очередным из накопленных выходов.
    */
    //--------------------------------------------------------------------------

    public abstract void multiStep(I_NeuronStep back) throws UniException;

    @Override
    protected float[] create(){
        float out[] = new float[depth*src.size()];
        for(int i=0;i<out.length;i++)
            out[i]=0;
        return out;
        }
    
    @Override
    public void synch(){}
    
    @Override
    public void step(I_NeuronStep back) throws UniException{
        inData[stepIdx]=(float[])(src.getSpikes().clone());
        stepIdx++;
        if (stepIdx==depth){
            stepIdx = 0;
            full = true;
            multiStep(back);
            }      
        }

    @Override
    public float getSpike(int idx) throws UniException {
        return !full ? 0.0f : outData[stepIdx][idx];
        }    
    @Override
    public float[] getSpikes() throws UniException {
        return !full ? emptyData : outData[stepIdx];
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
                    return !full ? 0.0f : outData[stepIdx][ii];
                    }
                };
            }
        }
    @Override
    public void reset(FFTParams pars) throws UniException {
        super.reset(pars);
        inData = new float[depth][];
        outData = new float[depth][];
        stepIdx=0;
        full=false;
        emptyData = new float[size()];
        for(int i=0;i<emptyData.length;i++)
            emptyData[i]=0;        
        }

    @Override
    public String getTypeName() {
        return "???";
        }

    @Override
    public void save(BufferedWriter out) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    @Override
    public String save() throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    @Override
    public void load(Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    @Override
    public String getFormatLabel() {
        return "???";
        }
}
