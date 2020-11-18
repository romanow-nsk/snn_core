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
public class FFTHarmonicQuickTime extends FFTHarmonicShortTime{
    public FFTHarmonicQuickTime(){ super(160,10,75,7,20,200); }
    @Override
    public String getTypeName() {
        return "5 октав 20 мс с паузой";
        }

}
