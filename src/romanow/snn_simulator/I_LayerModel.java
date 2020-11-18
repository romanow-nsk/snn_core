/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import romanow.snn_simulator.fft.FFTParams;
import romanow.snn_simulator.layer.LayerStatistic;

/**
 *
 * @author romanow
 */
public interface I_LayerModel extends I_Layer,I_TypeName,I_TextStream{
    public void reset(FFTParams pars) throws UniException;
    public float[] step(float[] in, I_NeuronStep back) throws UniException;
    public void setSubToneCount(int cnt);
    public void initModel(int subTones, I_NetParams params) throws Exception;
    public void numerateUID();
    public LayerStatistic getStatistic(int index);
    public TypeFactory<LayerStatistic> getFactory();
}
