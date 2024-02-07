package com.anurag.synth;

import com.anurag.synth.utils.utils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC.*;

import java.util.function.Supplier;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

class Audiothread extends Thread{
    static final int BUFFER_SIZE = 512;
    static final int BUFFER_COUNT = 8;
    private final Supplier<short[]> bufferSuplier;
    private final int[] buffers = new int[BUFFER_COUNT];
    private final long device = alcOpenDevice(alcGetString(0,ALC_DEVICE_SPECIFIER));
    private final long context = alcCreateContext(device, new int[1]);
    private final int source;
    private int BufferIndex;
    private boolean closed;
    private boolean running;

    Audiothread(Supplier<short[]>bufferSuplier){
        this.bufferSuplier = bufferSuplier;
        alcMakeContextCurrent(context);
        AL.createCapabilities(ALC.createCapabilities(device));
        source = alGenSources();
        for(int i =0;i<BUFFER_COUNT;i++){
            //Buffer samples
            BufferSamples(new short[0]);
        }
        alSourcePlay(source); // it will catch internal exception
        catchInternalException();
        start();
    }
    boolean isRunning(){
        return running;
    }
    @Override
    public synchronized void run(){
        while(!closed){
            while(!running){
                    utils.invokeProcedure(this::wait , false);
            }
            int processedBuffs = alGetSourcei(source, AL_BUFFERS_PROCESSED);
            for(int i = 0;i<processedBuffs;i++){
                short[] sample = bufferSuplier.get();
                if (sample == null){
                running =  false;
                break;
                }
                alDeleteBuffers(alSourceUnqueueBuffers(source));
                buffers[BufferIndex] = alGenBuffers();
                BufferSamples(sample);
            }
            if(alGetSourcei(source,AL_SOURCE_STATE) != AL_PLAYING){
                alSourcePlay(source);
            }
            catchInternalException();
        }
        alDeleteSources(source);
        alDeleteBuffers(buffers);
        alcDestroyContext(context);
        alcCloseDevice(device);
    }
    synchronized void triggerPlayback(){
        running = true;
        notify();
    }
     void close(){
        closed = true;
        //break out of the loop
         triggerPlayback();
     }
    private void BufferSamples (short[] sample){
        int Buff = buffers[BufferIndex++];
        alBufferData(Buff,AL_FORMAT_MONO16,sample,Synthesis.AudioInfo.SampleRate);
        alSourceQueueBuffers(source,Buff);
        BufferIndex %= BUFFER_COUNT; //
    }
    private void catchInternalException(){
        int err = alcGetError(device);
        if(err != ALC_NO_ERROR){
            throw new OpenALException(err);
        }
    }
}