/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import romanow.snn_simulator.GBL;
import romanow.snn_simulator.I_LayerModel;
import romanow.snn_simulator.I_Neuron;
import romanow.snn_simulator.I_NeuronOutput;
import romanow.snn_simulator.I_Spike;
import romanow.snn_simulator.Token;
import romanow.snn_simulator.TypeFactory;
import romanow.snn_simulator.lang.NLM_Constructive;

/**
 *
 * @author romanow
 */
public class ModelFactory extends TypeFactory<I_LayerModel>{
    public ModelFactory(){
        generate("romanow.snn_simulator.model",I_LayerModel.class);        
        }
    public I_LayerModel load(BufferedReader in) throws IOException{
        Token tk = new Token(in);
        tk.nextLine();
        String name = tk.get();
        NLM_Proxy model = (NLM_Proxy)getByName(GBL.LoadedModel);
        I_LayerModel loaded = new NLM_Constructive();
        loaded.load(tk, new HashMap<Integer,I_NeuronOutput>());
        model.setOrig(loaded);
        return model;
        }
    
}
