package com.anurag.synth;
import com.anurag.synth.utils.Wrapper;
import com.anurag.synth.utils.utils;
import javax.swing.*;
import java.awt.event.ItemEvent;


public class oscillator extends SynthControlContainer {

    private WaveTable wavetable = WaveTable.Sine;
    private Wrapper<Integer> toneoffset = new Wrapper<>(0);
    private Wrapper<Integer> volume = new Wrapper<>(100);
    private int wavetableStepSize;
    private int wavetableIndex;
    private static final int Tone_Offset_Limit = 2000;
    private double keyfrequency;

    public oscillator(Synthesis synth){
        super(synth);
        JComboBox<WaveTable> comboBox = new JComboBox<>(WaveTable.values());
        comboBox.setSelectedItem(WaveTable.Sine);
        comboBox.setBounds(10,10,75,25);
        comboBox.addItemListener(l ->{
            if(l.getStateChange() == ItemEvent.SELECTED){
                wavetable  = (WaveTable)l.getItem();
            }
            synth.updateWaveView();
        });
        add(comboBox);
        JLabel toneParameter = new JLabel("x0.00");
        toneParameter.setBounds(165,65,50,25);
        toneParameter.setBorder(utils.WindowDesign.LINE_BORDER);
        add(toneParameter);

        JLabel volumeText = new JLabel("Volume");
        volumeText.setBounds(225,40,50,25);
        add(volumeText);

        JLabel volumeParameter = new JLabel("100%");
        volumeParameter.setBounds(222,65,50,25);
        volumeParameter.setBorder(utils.WindowDesign.LINE_BORDER);
        utils.ParameterHandling.addParameterMouseListners(volumeParameter, this,0, 100, 1,volume, () ->{
            volumeParameter.setText(" "+ volume.value+"%");
            synth.updateWaveView();
        });
        add(volumeParameter);

        JLabel tonetext = new JLabel("Tone");
        tonetext.setBounds(172,40,75,25);
        add(tonetext);
        utils.ParameterHandling.addParameterMouseListners(toneParameter, this,-Tone_Offset_Limit ,Tone_Offset_Limit ,1,toneoffset, () ->
        {
            applyToneOffset();
            toneParameter.setText("x" + String.format("%.3f", getToneOffset()));
            synth.updateWaveView();
        });
        setSize(275,100);
        setBorder(utils.WindowDesign.LINE_BORDER);
        setLayout(null);
    }

    public double getNextSample(){
        double sample = wavetable.getSample()[wavetableIndex] * getVolume();
        wavetableIndex = (wavetableIndex + wavetableStepSize) % WaveTable.SIZE;
        return sample;
    }
    public void setFrequency(double Frequency){
        keyfrequency =  Frequency;
        //applies the tone offset
        applyToneOffset();
    }
    public double[] getSampleWaveForm(int numberSamples){
        double[] samples = new double[numberSamples];
        double frequency = 1.0/(numberSamples / (double)Synthesis.AudioInfo.SampleRate) * 3.0;
        int index = 0;
        int stepSize = (int)(wavetable.SIZE * utils.Math.offsetTone(frequency, getToneOffset())/ Synthesis.AudioInfo.SampleRate);
        for (int i = 0;i<numberSamples;++i){
            samples[i] = wavetable.getSample()[index] * getVolume();
            index = (index + stepSize) % WaveTable.SIZE;
        }
        return samples;
    }
    private double getToneOffset(){
        return toneoffset.value / 1000d;
    }
    private double getVolume(){
        return volume.value / 100.0;
    }
    public double nextSample(){
        double sample = wavetable.getSample()[wavetableIndex] * getVolume();
        wavetableIndex = (wavetableIndex + wavetableStepSize) % wavetable.SIZE;
        return sample;
    }
    private void applyToneOffset(){
        wavetableStepSize = (int)(WaveTable.SIZE * (utils.Math.offsetTone(keyfrequency,getToneOffset()))/Synthesis.AudioInfo.SampleRate);
    }
}
