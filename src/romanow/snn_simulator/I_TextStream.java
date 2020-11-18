/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author romanow
 */
public interface I_TextStream {
    public void save(BufferedWriter out) throws IOException;
    public String save() throws IOException;
    public void load(Token in, HashMap<Integer, I_NeuronOutput> map) throws IOException;
    public String getFormatLabel();
}
