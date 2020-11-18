/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.lang;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Vector;
import romanow.snn_simulator.I_Layer;
import romanow.snn_simulator.I_NeuronLayer;
import romanow.snn_simulator.layer.LayerFactory;
import romanow.snn_simulator.layer.LayerStatistic;
import romanow.snn_simulator.layer.NL_Integrate;
import romanow.snn_simulator.layer.NL_NeuronLayer;
import romanow.snn_simulator.neuron.N_BaseNeuron;
import romanow.snn_simulator.neuron.NeuronFactory;

/**
 *
 * @author romanow
 */
public class ModelCreator {
    private class DBField {
        public String name,value="";
        public int type=-1;
        public Method get,set;
        public Field field;
        DBField(String nm, int tp, Method get0, Method set0){ name=nm; type=tp; set=set0; get=get0; field=null; }
        DBField(String nm, int tp, Field fld0){ name=nm; type=tp; set=null; get=null; field=fld0; }
        }
    //--------------------------------------------------------------------------
    public static String dbTypes[]={"int","String","double","boolean","short","long","java.lang.String","float"};
    public final static byte dbInt=0,dbString=1,dbDouble=2,dbBoolean=3,dbShort=4,dbLong=5,dbString2=6,dbFloat=7;  //  ID-ы сериализуемых типов
    public int getDbTypeId(String ss){
        int i;
        for (i=0;i<dbTypes.length;i++)
            if (dbTypes[i].equals(ss)) return i;
        return -1;
        }    
    //----------------------------- Реализация через Fields -------------------
    final public Vector<DBField> getFields(Object src) throws Exception{
        Class cls=src.getClass();
        Vector<DBField> out=new Vector();
        for(;cls!=null;cls=cls.getSuperclass()){    // Цикл по текущему и базовым
            Field fld[]=cls.getDeclaredFields();    // 
            for(int i=0;i<fld.length;i++){          // Перебор всех полей
                fld[i].setAccessible(true);         // Сделать доступными private-поля
                String tname=fld[i].getType().getName();
                int type=getDbTypeId(tname);
                String name=fld[i].getName();
                if (type==-1) continue;
                if ((fld[i].getModifiers() & Modifier.TRANSIENT)!=0) continue;
                if ((fld[i].getModifiers() & Modifier.STATIC)!=0) continue;
                out.add(new DBField(name,type,fld[i]));
                }
            }
        return out;
        }
    //--------------------------------------------------------------------------
    public void setFieldValue(Vector<DBField> flds, Object obj, 
        String fldName, String value) throws Exception{
        DBField ff=null;
        for(int i=0;i<flds.size();i++){
            ff=flds.get(i);
            if (ff.name.compareTo(fldName)==0){
                switch(ff.type){
    	case dbInt:     ff.field.setInt(obj, Integer.parseInt(value)); break;
    	case dbShort:	ff.field.setShort(obj, Short.parseShort(value)); break;
    	case dbLong:	ff.field.setLong(obj, Long.parseLong(value)); break;
    	case dbFloat:	ff.field.setFloat(obj, Float.parseFloat(value)); break;
    	case dbDouble:	ff.field.setDouble(obj, Double.parseDouble(value)); break;
    	case dbBoolean: ff.field.setBoolean(obj, Boolean.parseBoolean(value)); break;
    	case dbString2:	
        case dbString:	ff.field.set(obj, value); break;
                        }
                    return;
                    }
                }
            throw new Exception("Отсутстует поле "+obj.getClass().getSimpleName()+":"+fldName);
            }
    //--------------------------------------------------------------------------
    public String getFieldValue(Vector<DBField> flds, Object obj, 
        String fldName) throws Exception{
        DBField ff=null;
        String ss="";
        for(int i=0;i<flds.size();i++){
            ff=flds.get(i);
            if (ff.name.compareTo(fldName)==0){
                switch(ff.type){
    	case dbInt:     ss = ""+ff.field.getInt(obj); break;
    	case dbShort:	ss = ""+ff.field.getShort(obj); break;
    	case dbLong:	ss = ""+ff.field.getLong(obj); break;
    	case dbFloat:	ss = ""+ff.field.getFloat(obj); break;
    	case dbDouble:	ss = ""+ff.field.getDouble(obj); break;
    	case dbBoolean: ss = ""+ff.field.getBoolean(obj); break;
    	case dbString2:	
        case dbString:	ss = ""+ff.field.get(obj); break;
                        }
                    return ss;
                    }
                }
            throw new Exception("Отсутстует поле "+obj.getClass().getSimpleName()+":"+fldName);
            }
    public NLM_Constructive compile(String src, int subToneCount) throws Exception{
        LayerFactory layerFactory = new LayerFactory();
        NeuronFactory neuronFactory = new NeuronFactory();
        NLM_Constructive out = new NLM_Constructive();
        out.initModel(subToneCount,null);
        XMLParser xx = new XMLParser();
        ModelDescript desc = (ModelDescript)xx.fromXML(src);        // Парсить описание
        if (desc.statistics == null)
            desc.statistics = new Vector();
        for (int i=0;i<desc.layers.size();i++){                     // Обработка слоев
            Layer lr = desc.layers.get(i);
            if (lr instanceof NeuronLayer){                         // Слой нейронный
                NL_NeuronLayer nl = new NL_NeuronLayer();
                N_BaseNeuron nr = neuronFactory.getByName(lr.type); // Прототип по имени
                Vector<DBField> flds = getFields(nr);               // Поля нейрона
                nl.setObjectName(lr.name);                          // Имя слоя
                nl.createLayer(nr, out.size());                     // Создать слой
                if (lr.params!=null){
                    for(int j=0;j<lr.params.size();j++){ 
                        Param par = lr.params.get(j);                   // Параметры в нейроны
                        for(int k=0;k<nl.size();k++)
                            setFieldValue(flds,nl.get(k),par.name,par.value);
                        }
                    }
                out.add(nl);                                        // Добавить слой
                }
            else{
                NL_Integrate nl = layerFactory.getByName(lr.type);  // Слой по имени
                Vector<DBField> flds = getFields(nl);               // Поля нейрона
                nl.setObjectName(lr.name);                          // Имя слоя
                nl.createLayer(null, out.size());
                if (lr.params!=null){
                    for(int j=0;j<lr.params.size();j++){ 
                        Param par = lr.params.get(j);                   // Параметры в слой
                        setFieldValue(flds,nl,par.name,par.value);
                        }
                    }
                out.add(nl);                                        // Добавить слой
                }
            }
        for(int i=0;i<desc.links.size();i++){
            Link ln = desc.links.get(i);
                I_NeuronLayer lrOut = out.getLayerByName(ln.to);
                if (lrOut==null)
                    throw new Exception("Не найден выходной слой связи "+ln.from+"-"+ln.to);
                I_Layer lrIn;
                if (ln.from.compareTo("Вход")==0){
                    lrIn = out.getInputs();
                    }
                else{
                    lrIn = out.getLayerByName(ln.from);
                    if (lrIn==null)
                       throw new Exception("Не найден входной слой связи "+ln.from+"-"+ln.to);
                    }
                if (lrOut instanceof NL_Integrate){         // Слой все синапсы ВМЕСТЕ
                    ((NL_Integrate)lrOut).addSynapse(lrIn);
                    }
                else{
                    switch (ln.type){               // Связать по типу связи
            case 0:     lrOut.addInputsLinear(ln.param, lrIn); break;
            case 1:     lrOut.addSynapse(lrIn); break;
            case 2:     lrOut.addSynapseShifted(ln.param, lrIn); break;
                        }
                    }
                }
        for(int i=0;i<desc.statistics.size();i++){
            Statistic ln = desc.statistics.get(i);
            I_NeuronLayer nl;
            if (ln.layer.compareTo("Вход")==0)
                out.statistics.add(new LayerStatistic(out.inputs,ln.name));
            else{
                nl= out.getLayerByName(ln.layer);
                if (nl==null)
                    throw new Exception("Не найден слой сбора статистики: "+ln.layer);
                out.statistics.add(new LayerStatistic(nl,ln.name));
                }
            }
        if (desc.output==null)
            throw new Exception("Не указано подключение выхода");
        int idx = desc.getLayerIdxByName(desc.output.name);
        if (idx==-1)
            throw new Exception("Не найден слой подключения выхода: "+desc.output.name);
        out.setOutLayerIdx(idx);
        if (desc.input==null)           // Вход нужен ДЛЯ СПРАВКИ ПРИ СЕРИАЛИЗАЦИИ
            idx=-1;
        else
            idx = desc.getLayerIdxByName(desc.input.name);
        out.setInLayerIdx(idx);
        out.numerateUID();              // Поронумеровать при создании
        return out;
        }
    public String createTestModel(){
        XMLParser xx = new XMLParser();
        ModelDescript model = new ModelDescript();
        NeuronLayer nl = new NeuronLayer("Слой 1","Нейрон Гаврилова");
        nl.add(new Param("DH","0.05"));
        nl.add(new Param("HMax","0.95"));
        model.layers.add(nl);
        NeuronLayer nl2 = new NeuronLayer("Слой 2","Повторитель");
        //nl2.add(new Param("spikeLevel","0.1"));
        model.layers.add(nl2);
        model.input = new Input("Слой 1");
        model.output = new Output("Слой 2");
        model.links.add(new Link("Слой 1","Слой 2",0,5));
        model.links.add(new Link("Вход","Слой 1",1,0));
        String ss = xx.toXML(model);
        return ss;
        }
    public static void main(String argv[]) throws Exception{
        XMLParser xx = new XMLParser();
        ModelCreator creator = new ModelCreator();
        String ss = creator.createTestModel();
        System.out.println(ss);
        ModelDescript model2 = (ModelDescript)xx.fromXML(ss);
        NLM_Constructive res = creator.compile(ss,3);
        /*
        ModelCreator creator = new ModelCreator();
        N_Gavrilov gav = new N_Gavrilov();
        Vector<DBField> flds = creator.getFields(gav);
        creator.setFieldValue(flds, gav, "HMax","2.5");
        System.out.println(creator.getFieldValue(flds, gav, "HMax"));
        */
        }
}
