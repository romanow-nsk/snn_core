/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.model;

import romanow.snn_simulator.I_NetParams;

public class NLM_Simple extends NLM_Base{
    public NLM_Simple(){
        super();
        }
    @Override
    public void initModel(int subTones, I_NetParams params) throws Exception{
        super.initModel(subTones, params);
        layer.createLayer(params.getNeuronProto(), size());
        layer.addInputsLinear(params.getSynapsesCount(), inputs);
        }
    @Override
    public String getTypeName() {
        return "Однослойная";
        }    
}
