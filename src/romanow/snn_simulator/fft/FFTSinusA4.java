/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import java.io.IOException;

/**
 *
 * @author romanow
 */
public class FFTSinusA4 extends FFTSinus{
    public FFTSinusA4(){
        super(1760,40,75);
        }
    @Override
    public String getTypeName() {
        return "1760(A4)";
        }
}
