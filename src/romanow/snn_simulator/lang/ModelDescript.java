/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.lang;

import java.util.Vector;

/** Описание модели !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 <model>
  <neurons name="aa" type="bb">
    <param name="cc" value="12"/>
    <param name="dd" value="ertyu"/>
  </neurons>
  <layer name="a1" type="b1">
    <params>
      <param name="c1" value="112"/>
      <param name="d1" value="1ertyu"/>
    </params>
  </layer>
</model>
*/
public class ModelDescript {
    public Vector<Layer> layers=new Vector();
    public Vector<Link> links=new Vector();
    public Vector<Statistic> statistics=new Vector();
    public Input input=null;
    public Output output=null;
    public int getLayerIdxByName(String name){
        for(int i=0;i<layers.size();i++)
            if (layers.get(i).name.compareTo(name)==0)
                return i;
        return -1;
        }
    public static void main(String argv[]) throws Exception{
        XMLParser xx = new XMLParser();
        ModelDescript model = new ModelDescript();
        NeuronLayer nl = new NeuronLayer("aa","bb");
        nl.add(new Param("cc","12"));
        nl.add(new Param("dd","ertyu"));
        model.layers.add(nl);
        Layer nl2 = new Layer("a1","b1");
        nl2.add(new Param("c1","112"));
        nl2.add(new Param("d1","1ertyu"));
        model.layers.add(nl2);
        model.input = new Input("aa");
        model.output = new Output("a1");
        model.links.add(new Link("aa","a1",0,0));
        model.statistics.add(new Statistic("a1","Вход"));
        model.statistics.add(new Statistic("a2","nnn"));
        String ss = xx.toXML(model);
        System.out.println(ss);
        ModelDescript model2 = (ModelDescript)xx.fromXML(ss);
        }       
}
