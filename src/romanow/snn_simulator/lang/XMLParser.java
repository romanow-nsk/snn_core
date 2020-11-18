/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.lang;

import com.thoughtworks.xstream.XStream;

/** Описание модели
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
  <link from="aa" to="a1" type="1"/>
  <input name="aa"/>
  <output name="a1"/>
</model>
 */
public class XMLParser extends XStream{
    public XMLParser(){
        super();
        //---- Включить значение поля в атрибуты тега --------------
        alias("model", ModelDescript.class);
        alias("neurons", NeuronLayer.class);
        alias("layer", Layer.class);
        alias("link", Link.class);
        alias("statistic", Statistic.class);
        useAttributeFor(Statistic.class, "name");
        useAttributeFor(Statistic.class, "layer");
        useAttributeFor(Layer.class, "name");
        useAttributeFor(Layer.class, "type");
        useAttributeFor(Link.class, "from");
        useAttributeFor(Link.class, "to");
        useAttributeFor(Link.class, "type");
        useAttributeFor(Link.class, "param");
        alias("param", Param.class);
        useAttributeFor(Param.class, "name");
        useAttributeFor(Param.class, "value");
        //aliasAttribute(ModelDescript.class, "stats","statistics");
        //---- Исключить имя массива из XML ------------------------
        //addImplicitCollection(XMLArray.class, "list");
        //---- Исключить имя ВЕКТОРА из XML ------------------------
        addImplicitArray(Layer.class, "params");
        addImplicitArray(ModelDescript.class, "links");
        //---------------- НЕ РАБОТАЕТ ------ ДВА ВЕКТОРА НЕЛЬЗЯ БЕЗЫМЯННЫХ
        //addImplicitArray(ModelDescript.class, "statistics");
        //addImplicitArray(ModelDescript.class, "layers");
        //----------------------------------------------------------------------
    }
    public static void main(String argv[]){
        XMLParser pars = new XMLParser();
        }
}
