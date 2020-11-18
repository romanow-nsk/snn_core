/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.neuron.N_DigitalSource;
import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.UniExcept;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_NeuronOutput;

/**
 *
 * @author romanow
 */
public class NL_DigitalSource implements I_Layer{
    private N_DigitalSource vec[]=null;
    public NL_DigitalSource(int size){
        vec = new N_DigitalSource[size];
        for(int i=0;i<size;i++)
            vec[i]=new N_DigitalSource();
        }
    public void setValues(float data[]) throws UniException{
        if (data.length !=vec.length)
            UniExcept.calcEx("Не совпадают размерности "+data.length+" "+vec.length);
        for(int i=0; i<vec.length; i++)
            vec[i].setSpike(data[i]);
        }
    @Override
    public int size() {
        return vec.length;
        }
    @Override
    public I_NeuronOutput get(int idx)  throws UniException{
        if (idx<0 || idx>=vec.length)
            UniExcept.calcEx("Выход за пределы вектора "+idx+" "+vec.length);
        return vec[idx];
        }
    @Override
    public float[] getSpikes() {
        float out[] = new float[vec.length];
        for(int i=0;i<vec.length;i++)
            out[i]=vec[i].getSpike();
        return out;
        }
    @Override
    public int getSubToneCount() {
        return size()/(FFT.Octaves*12);
        }
    public int numerateUID(int uid){
        for(int i=0;i<vec.length;i++){
            vec[i].setUID(uid++);
            }
        return uid;
        }
    public void toMap(int uid,HashMap<Integer,I_NeuronOutput> map){
        for(int i=0;i<vec.length;i++){
            map.put(uid++, vec[i]);
            }
        }
    public void save(BufferedWriter out) throws IOException {
        out.write("//Входы");
        out.newLine();
        out.write(".../NL_DigitalSource/"+size()+"/"+vec[0].getUID());
        out.newLine();
        }   
}
