package romanow.snn_simulator.model;

import romanow.snn_simulator.layer.NL_NeuronLayer;
import romanow.snn_simulator.UniException;
import romanow.snn_simulator.I_NeuronStep;
import romanow.snn_simulator.I_NetParams;
import romanow.snn_simulator.fft.FFTParams;


public class NLM_TwoLevelModel extends NLM_Base{
    protected NL_NeuronLayer layer2=null;
    public NLM_TwoLevelModel(){
        super();
        }
    @Override
    public void initModel(int subTones, I_NetParams params) throws Exception{
        super.initModel(subTones, params);
        layer2 = new NL_NeuronLayer();
        layer.createLayer(params.getNeuronProto(), size());
        layer2.createLayer(params.getNeuronProto(), size());
        layer2.addInputsLinear(params.getSynapsesCount(), inputs);
        layer.addInputsLinear(params.getSynapsesCount(), layer2);
        }
    @Override
    public void reset(FFTParams pars) throws UniException {
        layer.reset(pars);
        layer2.reset(pars);
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
        return "2 одинаковых";
        }        
}
