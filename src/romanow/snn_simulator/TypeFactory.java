/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator;

import java.util.Set;
import java.util.Vector;
import org.reflections.Reflections;

/**
 *
 * @author romanow
 */
public class TypeFactory<T extends I_Name> implements I_NamedFactory<T> {
    private Vector<T> list = new Vector();
    public void add(T val){
        list.add(val);
        }
    public String []createList(){
        String out[] = new String[list.size()];
        for(int i=0;i<list.size();i++){
            T vv = list.get(i);
            if (vv == null)
                out[i]="???";
            else
                out[i]=list.get(i).getName();
            }
        return out;
        }
    public T getByName(String name){
        T vv=null;
        for(int i=0;i<list.size();i++){
            vv = list.get(i);
            if (vv!=null && vv.getName().compareTo(name)==0)
                return vv;
            }
        return null;
        }
    //---------- Перебор классов в пакете и заполнение фабрики ----------------
    public void generate(String pkgIn, Class base){
        String clsName="";
        try {
            Reflections reflections = new Reflections(pkgIn);
            Set<Class<? extends I_TypeName>> subTypes = 
               reflections.getSubTypesOf(base);
            Object oo[] = subTypes.toArray();
            for (Object oo1 : oo) {
                Class cls = (Class) oo1;
                if (cls.isInterface())
                    continue;
                clsName = cls.getName();
                if (cls.getSimpleName().equals("NL_Integrate"))
                    continue;
                if (cls.getSimpleName().equals("NL_IntegrateBuffered"))
                    continue;
                try {
                    T obj = (T)Class.forName(clsName).newInstance();
                    if (obj!=null)
                        add(obj);
                    } catch(InstantiationException ee){
                        System.out.println("Не могу создать объект: "+clsName+" "+base.getSimpleName());
                        }
                }
        } catch(Throwable ee){
            System.out.println("Ошибка фабрики: "+clsName+" "+base.getSimpleName()+" Исключение:"+ee.toString());
            }
        }

}
