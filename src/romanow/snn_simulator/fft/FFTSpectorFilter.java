/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import org.apache.commons.math3.complex.Complex;

/**
 *
 * @author romanow
 */
public interface FFTSpectorFilter {
    public Complex convert(Complex in, float filterValue);
}
