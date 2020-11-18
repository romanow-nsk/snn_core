/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package romanow.snn_simulator;

import java.io.IOException;
import java.sql.SQLException;

/**
 *
 * @author romanow
 */
public class UniExcept{
    public final static String exceptTypes[] = {"Предупреждение","Ошибка исполнения","Программная ошибка","Ошибка настройки"};
    public final static int warning=0;
    public final static int runTime=1;
    public final static int bug=2;
    public final static int config=3;
    //------------------ Типовые ошибки ----------------------------------------
    public final static String fatal="Ошибка программирования";
    public final static String sql="Ошибка базы данных";
    public final static String net="Ошибка сети";
    public final static String format="Ошибка формата даннных";
    public final static String noFunc="Функция не реализована";
    public final static String other="Прочие ошибки";
    public final static String indirect="Ошибка удаленной компоненты";
    public final static String settings="Ошибка настроек";
    public final static String io="Ошибка в/в";
    public final static String calc="Ошибка формата/данных";
    public final static UniException calcEx(String mes){
        return new UniException(runTime, calc, mes);
        }
    public final static UniException calclEx(Throwable ee){
        return new UniException(runTime, calc, ee, true);
        }
    public final static UniException fatalEx(String mes, Throwable ee){
        return new UniException(bug, mes, ee, true);
        }
    public final static UniException fatalEx(Throwable ee){
        return new UniException(bug, fatal, ee, true);
        }
    public final static UniException fatalEx(String mes){
        return new UniException(bug, fatal+ ":" + mes, null, false);
        }
    public final static UniException warningEx(String mes){
        return new UniException(warning,mes);
        }
    public final static UniException sqlEx(Throwable ee){
        return new UniException(bug,sql,ee,false);
        }
    public final static UniException sqlEx(String mes){
        return new UniException(bug,sql,mes);
        }
    public final static UniException netEx(Throwable ee){
        return new UniException(runTime,net,ee,false);
        }
    public final static UniException netEx(String mes){
        return new UniException(runTime,net,mes);
        }
    public final static UniException formatEx(String mes){
        return new UniException(bug,format,mes);
        }
    public final static UniException noFuncEx(){
        return new UniException(config,noFunc);
        }
    public final static UniException noFuncEx(String mes){
        return new UniException(config,noFunc,mes);
        }
    public final static UniException otherEx(){
        return new UniException(bug,other);
        }
    public final static UniException otherEx(Throwable ee){
        return new UniException(bug, other, ee, true);
        }
    public final static UniException otherEx(String mes){
        return new UniException(bug,mes);
        }
    public final static UniException indirectEx(String mes){
        return new UniException(runTime,indirect,mes);
        }
    public final static UniException configEx(String mes){
        return new UniException(config,settings,mes);
        }
    public final static UniException ioEx(Throwable ee){
        return new UniException(runTime,io,ee,false);
        }
    public final static UniException ioEx(String mes){
        return new UniException(runTime,io,mes);
        }
    public final static UniException totalEx(Throwable ee){
      	if (ee instanceof SQLException)
            return sqlEx(ee);
        if (ee instanceof IOException) 
            return ioEx(ee);
        if (ee instanceof Error)
            return fatalEx(ee);
        if (ee instanceof UniException){
            UniException vv = (UniException)ee;
            vv.sysMessage = vv.userMessage + ":" +vv.sysMessage;
            vv.userMessage = indirect;
            return vv;
            }
        return otherEx(ee);
        }
}
