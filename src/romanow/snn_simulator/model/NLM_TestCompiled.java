/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.model;

import romanow.snn_simulator.lang.NLM_Constructive;
import romanow.snn_simulator.lang.ModelCreator;

public class NLM_TestCompiled extends NLM_Proxy{
    public NLM_TestCompiled() throws Exception{
        super();
        ModelCreator creator = new ModelCreator();
        String ss = creator.createTestModel();
        NLM_Constructive res = creator.compile(ss,4);   // Пока ТАК ЗАШИТЬ !!!!!
        setOrig(res);
        }
    @Override
    public String getTypeName() {
        return "Загруженный тест";
        }    
}
