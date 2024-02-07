package com.anurag.synth;

import com.anurag.synth.utils.utils;

enum WaveTable{
    Sine, Square, Sav, Triangle;
    static final int SIZE = 1764000;
    private final float[] sample = new float[SIZE];
    static{
        final double Basic_Frequency = 1d/ (SIZE /(double)Synthesis.AudioInfo.SampleRate);
        for (int i = 0;i<SIZE; ++i){

            double t = i / (double)Synthesis.AudioInfo.SampleRate;
            double tDivP = t /(1d / Basic_Frequency);
            Sine.sample[i] = (float)Math.sin(t * utils.Math.FreqToAgFreq(Basic_Frequency));
            Square.sample[i] = Math.signum(Sine.sample[i]);
            Sav.sample[i] = (float)(2d * (tDivP - Math.floor(0.5 + tDivP)));
            Triangle.sample[i] = (float)(2d * Math.abs(Sav.sample[i]) - 1d);
        }
    }
    float[] getSample(){
        return sample;
    }
}