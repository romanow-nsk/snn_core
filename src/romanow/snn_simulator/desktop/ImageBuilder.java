/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.desktop;

/**
 *
 * @author romanow
 */
public class ImageBuilder {
    private static IImage iTypes[]={
        new NoImage(),
        new FileImage(null),
        new VerticalLine(),
        new HorizontalLine()
        };
    public static IImage getImageByType(int id){
        if (id<0 || id>=iTypes.length)
            return null;
        return iTypes[id];
        }
    public static String[] getImagesList(){
        String out[]= new String[iTypes.length];
        for(int i=0; i<iTypes.length;i++)
            out[i]=iTypes[i].getTeachImageName();
        return out;
        }
}
