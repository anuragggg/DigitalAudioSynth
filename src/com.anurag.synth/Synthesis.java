package com.anurag.synth;
import com.anurag.synth.utils.utils;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;

public class Synthesis {
    private static final HashMap<Character, Double> Key_Frequencies = new HashMap<>();

    private boolean shouldGenerate;
    private final oscillator[] oscillators = new oscillator[3];
    private final WaveView waveView = new WaveView(oscillators);
    private JFrame frame =  new JFrame("Synthesizer");
    private final Audiothread thread = new Audiothread(()->
    {
        if(!shouldGenerate){
            return null;
        }
        short[] s = new short[Audiothread.BUFFER_SIZE];
        for(int i = 0;i <Audiothread.BUFFER_SIZE;++i){
            double d =0;
            for(oscillator o : oscillators){
                d += o.nextSample() / oscillators.length;
            }
            s[i] = (short)(Short.MAX_VALUE * d);
        }
        return s;
    });
    public final KeyAdapter keyAdapter = new KeyAdapter() {
        @Override
            public void keyPressed(KeyEvent e) {
            if(!Key_Frequencies.containsKey(e.getKeyChar())){
                return;
            }
                if(!thread.isRunning()){
                    for (oscillator o : oscillators){
                        o.setFrequency(Key_Frequencies.get(e.getKeyChar()));
                    }
                    shouldGenerate = true;
                    thread.triggerPlayback();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                shouldGenerate = false;

        }
    };

    static {
        final int Starting_Key = 16;
        final int Key_Frequencies_increment = 2;
        final char[] keys = "zxcvbnm,./asdfghjkl;'qwertyuiop[]`".toCharArray();
        for(int i = Starting_Key, key = 0; i < keys.length * Key_Frequencies_increment + Starting_Key; i += Key_Frequencies_increment,++key){
            Key_Frequencies.put(keys[key], utils.Math.getKeyfrequency(i));
        }
        for(Double d : Key_Frequencies.values()){
            System.out.println(d);
        }
    }

    Synthesis(){
       int y = 0;
       for(int i = 0;i<oscillators.length;++i){
           oscillators[i] = new oscillator(this);
           oscillators[i].setLocation(5, y);
           frame.add(oscillators[i]);
           y += 105;
       }
       waveView.setBounds(290,0,310,310);
       frame.add(waveView);
        frame.addKeyListener(keyAdapter);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thread.close();
            }
        });
        frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        frame.setSize(640,384);
        frame.setResizable(true);
        frame.setLayout(null);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public KeyAdapter getKeyAdapter() {
        return keyAdapter;
    }
    public void updateWaveView(){
        waveView.repaint();
    }

    public static class AudioInfo{
        public static final int SampleRate = 44100;
    }
}
