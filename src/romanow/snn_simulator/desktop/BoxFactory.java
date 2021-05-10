/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

import javax.swing.JComboBox;
import romanow.snn_simulator.I_Name;
import romanow.snn_simulator.I_NamedFactory;
import romanow.snn_simulator.I_TypeName;
import romanow.snn_simulator.TypeFactory;

/**
 *
 * @author romanow
 */
public class BoxFactory<T extends I_Name>{ //666666666
    public interface BoxFactoryCallBack<T>{
        public void getSelected(T selectedItem);
        }
    private I_NamedFactory<T> factory;
    private JComboBox box;
    public T getSelected(){
        return factory.getByName(box.getSelectedItem().toString());
        }
    public I_NamedFactory<T> getFactory(){ return factory; }
    public BoxFactory(I_NamedFactory<T> factory,JComboBox box,final BoxFactoryCallBack<T> back){
        this.box = box;
        this.factory = factory;
        box.removeAllItems();
        String names[] = factory.createList();
        box.addItem("...");
        for(int i=0;i<names.length;i++)
            box.addItem(names[i]);
        final I_NamedFactory<T> factory1 = factory;
        box.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                back.getSelected(factory1.getByName((String)evt.getItem()));
                }
            });
        }
    public void setSelectedName(String name){
        box.setSelectedItem(name);
        }
    public void setSelectedIndex(int index){
        box.setSelectedIndex(index);
        }
    public String getSelectedName(){
        return (String)box.getSelectedItem();
        }
    public int getSelectedIndex(){
        return box.getSelectedIndex();
        }
}
