package com.anurag.synth;

import com.anurag.synth.utils.utils;
import javax.swing.*;
import java.awt.*;
import java.util.function.Function;

public class WaveView extends JPanel{
    private oscillator[] oscillators;
    public WaveView (oscillator[] oscillators){
        this.oscillators = oscillators;
        setBorder(utils.WindowDesign.LINE_BORDER);
    }

    @Override
    public void paintComponent (Graphics graphics)
    {
        final int PAD = 25;
        Graphics2D graphics2D = (Graphics2D)graphics;
        super.paintComponent(graphics);
        int numberSamples= getWidth() - PAD * 2;
        double[] MixedSamples = new double[numberSamples];
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        for(oscillator oscillator: oscillators){
            double[] samples = oscillator.getSampleWaveForm(numberSamples);
            for(int i = 0;i<samples.length;++i){
                MixedSamples[i] += samples[i] / oscillators.length;
            }
        }
        int midY = getHeight() / 2;
        Function<Double, Integer> sampleToY = sample -> (int)(midY + sample * (midY - PAD));

        graphics2D.drawLine(PAD, midY, getWidth() - PAD, midY);
        graphics2D.drawLine(PAD,PAD, PAD, getHeight()- PAD);
        for(int i= 0; i<numberSamples; ++i){
            int nextY = i ==numberSamples - 1 ? sampleToY.apply(MixedSamples[i]):sampleToY.apply(MixedSamples[i+1]);
            graphics2D.drawLine(PAD + i, sampleToY.apply(MixedSamples[i]),PAD + i + 1,nextY);
        }

    }
}
