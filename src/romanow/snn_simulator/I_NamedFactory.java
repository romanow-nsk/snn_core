/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author romanow
 */
public interface I_NamedFactory<T> {
    public String []createList();
    public T getByName(String name);
    public ArrayList<T> getFactoryContent();
    }
