package romanow.snn_simulator.model;

import romanow.snn_simulator.layer.NL_NeuronLayer;
import romanow.snn_simulator.neuron.N_FreqUp;
import romanow.snn_simulator.neuron.N_BandAndBurst;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_NetParams;
import romanow.snn_simulator.fft.FFTParams;

/**
 *
 * @author romanow
 */
public class NLM_Birds extends NLM_Base{
    protected NL_NeuronLayer layer2=null;
    public NLM_Birds(){
        super();
        }
    @Override
    public void initModel(int subTones, I_NetParams params) throws Exception{
        super.initModel(subTones, params);
        layer2 = new NL_NeuronLayer();
        layer.createLayer(new N_BandAndBurst(), size());
        layer2.createLayer(new N_FreqUp(), size());
        layer2.addInputsLinear(1, inputs);
        layer.addInputsLinearWithout(5, layer2);    // Без краев
        }
    
    @Override
    public void reset(FFTParams pars) {
        for(int i=0;i<size();i++){
            layer.get(i).reset();
            layer2.get(i).reset();
            }
        }
    @Override
    public float[] step(float[] in, I_NeuronStep back)  throws UniException{
        inputs.setValues(in);
        layer.step(back);
        layer2.step(back);
        layer.synch();
        layer2.synch();
        return layer.getSpikes();
        }
    @Override
    public String getTypeName() {
        return "Птички";
        }        
}
