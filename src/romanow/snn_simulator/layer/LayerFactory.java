/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.layer;

import romanow.snn_simulator.TypeFactory;

/**
 *
 * @author romanow
 */
public class LayerFactory extends TypeFactory<NL_Integrate>{
    public LayerFactory(){
        generate("romanow.snn_simulator.layer",NL_Integrate.class);
        generate("romanow.snn_simulator.layer",NL_IntegrateBuffered.class);
        }
}
