/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

import romanow.snn_simulator.fft.FFT;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_Spike;

// Коннектор с объединением по октавам - прокси
public class NL_OctaveConnector  implements I_Layer{
    private I_Layer src=null;
    public NL_OctaveConnector(I_Layer src){
        this.src = src;
        }
    @Override
    public int size() {
        return src.size();
        }
    @Override
    public I_NeuronOutput get(int i) throws UniException {
        int oct = src.getSubToneCount() * 12;
        int idx = i/oct + (i%oct)*FFT.Octaves;   
        System.out.println(i+" "+idx);
        return src.get(idx);
        }
    @Override
    public float[] getSpikes()  throws UniException {
        int oct = src.getSubToneCount() * 12;
        float out[]= new float[src.size()];
        for(int i=0;i<src.size();i++){
            int idx = i/oct + (i%oct)*FFT.Octaves;   
            out[idx] = (float)(src.get(i).getSpike());
            }
        return out;
        }
    @Override
    public int getSubToneCount() {
        return src.getSubToneCount();
        }
}
