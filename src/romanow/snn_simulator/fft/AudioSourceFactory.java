/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package romanow.snn_simulator.fft;

import romanow.snn_simulator.TypeFactory;

/**
 *
 * @author romanow
 */
public class AudioSourceFactory extends TypeFactory<FFTAudioSource>{
    public AudioSourceFactory(){
        generate("romanow.snn_simulator.fft",FFTAudioSource.class);
        }
}
