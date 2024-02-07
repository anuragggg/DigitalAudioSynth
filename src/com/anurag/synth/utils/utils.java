package com.anurag.synth.utils;

import com.anurag.synth.SynthControlContainer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import static java.lang.Math.*;
public class utils {
    public static void invokeProcedure(Procedure procedure, boolean printStackTrace) {
        try{
            procedure.invoke();
        }
        catch (Exception e) {
          if(printStackTrace)
          {
              e.printStackTrace();
          }
        }
    }
    public static class ParameterHandling{
        public static final Robot Parameter_Robot;
        static {
            try{
                Parameter_Robot = new Robot();
            }
            catch (AWTException e){
                throw new ExceptionInInitializerError("Cannot Construct Robot Instance");
            }
        }
        private ParameterHandling() {}
        public static void addParameterMouseListners(Component component, SynthControlContainer container, int minValue, int maxValue, int valueStep, Wrapper<Integer>parameter, Procedure onChangeProcedure){
            component.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    final Cursor Blank_Cursor = Toolkit.getDefaultToolkit().createCustomCursor(new BufferedImage(16,16,
                            BufferedImage.TYPE_INT_ARGB),new Point(0,0),"blank_cursor");
                    component.setCursor(Blank_Cursor);
                    container.setMouseClickLocation(e.getLocationOnScreen());
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    component.setCursor(Cursor.getDefaultCursor());
                }
            });
            component.addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if(container.getMouseClickLocation().y != e.getYOnScreen()){
                        boolean mouseMovedup = container.getMouseClickLocation().y - e.getYOnScreen() > 0;
                        if(mouseMovedup && parameter.value < maxValue){
                            parameter.value += valueStep;
                        }
                        else if(!mouseMovedup && parameter.value > minValue)
                        {
                            parameter.value -= valueStep;
                        }
                        if(onChangeProcedure != null){
                            invokeProcedure(onChangeProcedure, true);
                        }
                        Parameter_Robot.mouseMove(container.getMouseClickLocation().x, container.getMouseClickLocation().y);
                    }
                }
            });
        }
    }
    public static class WindowDesign{
        public static final Border LINE_BORDER = BorderFactory.createLineBorder(Color.black);
    }
    public static class Math{
        public static double offsetTone(double baseFrequency, double frequencyMultiplier)
        {
            return baseFrequency * pow(2.0, frequencyMultiplier);
        }
        public static double FreqToAgFreq(double freq){
            return 2*PI*freq;
        }
        public static double getKeyfrequency(int keynum){
            return pow(root(2,12), keynum - 49) * 440;

        }
        public static double root(double num, double root){
            return pow(E, log(num) / root);
        }

    }
}
