/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import romanow.snn_simulator.data.FunDo;

/**
 *
 * @author romanow
 */
public interface I_BinaryStream {
    public void save(DataOutputStream out) throws IOException;
    public void load(DataInputStream out) throws IOException;
}
