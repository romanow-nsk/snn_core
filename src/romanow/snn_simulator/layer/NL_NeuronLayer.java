/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.neuron.N_BaseNeuron;
import java.util.Vector;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.UniExcept;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_Neuron;
import romanow.snn_simulator.I_NeuronLayer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_ObjectName;
import romanow.snn_simulator.Token;
import romanow.snn_simulator.fft.FFTParams;

/**
 *
 * @author romanow
 */
public class NL_NeuronLayer extends Vector<N_BaseNeuron> implements I_NeuronLayer,I_ObjectName{
    // size И get наследуются от вектора
    private String name="...";
    private I_Neuron proto;
    @Override
    public void createLayer(I_Neuron proto, int size) throws Exception{
        this.proto = proto;
        for(int i=0;i<size;i++)        // Конструирует по прототипу
            try {
                N_BaseNeuron one = (N_BaseNeuron)proto.getClass().newInstance();
                one.setIndex(i);
                add(one);
            } catch (Exception ex) { UniExcept.calclEx(ex); }
        }
    @Override
    public int numerateUID(int uid){
        for(int i=0;i<size();i++)
            get(i).setUID(uid++);
        return uid;
        }
    @Override
    public I_Neuron findByUID(int uid){
        for(int i=0;i<size();i++)
            if (get(i).getUID()==uid)
                return get(i);
        return null;
        }
    
    @Override
    public float[] getSpikes(){
        float out[] = new float[size()];
        for(int i=0;i<size();i++)
            out[i] = get(i).getSpike();
        return out;
        }
    @Override
    public void step(I_NeuronStep back){
        for(int i=0;i<size();i++)
            get(i).step(back);
        }
    @Override
    public void synch(){
        for(int i=0;i<size();i++)
            get(i).synch();
        }
    @Override
    public void addInputsLinear(int size,I_Layer src) throws UniException{
        for(int i=0;i<size();i++)
            get(i).addInputsLinear(i, size, src);
        }
    public void addInputsLinearWithout(int size,I_Layer src) throws UniException{
        for(int i=size/2+1;i<size()-size/2-1;i++)
            get(i).addInputsLinear(i, size, src);
        }
    public void addInputsOctave(int octaveStep, int size, I_Layer src) throws UniException {
        for(int i=0;i<size();i++)
            get(i).addInputsOctave(i, octaveStep, size, src);
        }
    //---------------- Добавить выходы соседей на вход -------------------------
    public void addNeighbors() throws UniException {
        addNeighbors(1);
        }
    public void addNeighbors(int n) throws UniException {
        int sz = size();
        for(int i=0;i<sz;i++){
            for(int j=1; j<=n; j++){
                if (i-j>=0)
                    get(i).addSynapse(get(i-j),1);
                if (i+j<sz)
                    get(i).addSynapse(get(i+j),1);
                }
            }
        }
    @Override
    public int getSubToneCount() {
        return size()/(FFT.Octaves*12);
        }
    @Override
    public void addSynapse(I_Layer src) throws UniException{
        for(int i=0;i<size();i++){
            get(i).addSynapse(src.get(i),1);
            }
        }
    public void addSynapseShifted(int offset,I_Layer src) throws UniException{
        for(int i=0;i<size();i++){
            int idx = i+offset;
            if (idx<0)
                idx=0;
            if (idx>=size())
                idx=size()-1;
            get(i).addSynapse(src.get(idx),1);            
            }
        }

    @Override
    public void reset(FFTParams pars) throws UniException {
        for(int i=0;i<size();i++)
            get(i).reset();
        }
    @Override
    public float getSpike(int idx) throws UniException {
        return get(idx).getSpike();
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
    public String getTypeName() {
        return GBL.NeuronLayerName;
        }
    @Override
    public String getName() {
        return getTypeName();
        }    
    //------------------------- Загрузка / сохранение ------------------------
    @Override
    public void save(BufferedWriter out) throws IOException {
        out.write("//"+getFormatLabel());
        out.newLine();
        out.write(getObjectName()+"/"+getTypeName()+"/"+proto.getTypeName()+"/"+size());
        out.newLine();
        out.write("//"+proto.getFormatLabel());
        out.newLine();
        out.write("//<Номер нейрона>/<Даннные нейрона>");
        out.newLine();
        for(int i=0;i<size();i++){
            out.write(""+get(i).getUID()+"/"+get(i).save());
            out.newLine();
            }
        }
    @Override
    public void load(Token token, HashMap<Integer, I_NeuronOutput> map) throws IOException {
        for(int i=0;i<size();i++){
            N_BaseNeuron nr = get(i);
            token.nextLine();
            int uid = token.getInt();
            nr.setUID(uid);
            nr.load(token, map);
            }
    }

    @Override
    public String save() throws IOException {
        throw new IOException("Ошибка формата: "+getClass().getSimpleName());
        }
    @Override
    public String getFormatLabel() {
        return "<Имя слоя>/<Тип слоя>/<Тип нейрона>/<Количество нейронов>";
        }

    @Override
    public void setLearningMode(boolean learinig) {
        for(int i=0;i<size();i++)
            get(i).setLearningMode(learinig);
        }
    
}
