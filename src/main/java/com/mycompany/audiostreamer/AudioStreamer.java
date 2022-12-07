/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */
package com.mycompany.audiostreamer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import spark.Spark;

/**
 *
 * @author jokin
 */
public class AudioStreamer {

    public static void main(String[] args) throws LineUnavailableException, IOException, UnsupportedAudioFileException {
        TargetDataLine line = getTargetDataLine();
        line.open();
        
        Spark.staticFileLocation("/public");
        Spark.webSocket("/sound", SoundWebSocketHandler.class);
        Spark.init();
        
        startBroadcasting(line);
    }

    public static void startBroadcasting(TargetDataLine line) throws IOException, LineUnavailableException, UnsupportedAudioFileException {
        line.start();
        try ( InputStream is = new AudioInputStream(line)) {
            byte[] buffer = new byte[line.getBufferSize()];
            while (is.read(buffer) > 0) {
                //transform(buffer);
                //System.out.println(Arrays.toString(buffer));
                SoundWebSocketHandler.broadcastSoundData(buffer);
            }
        }
        line.stop();
    }

    private static void transform(byte[] data) {
        double[] transformed = new double[data.length];

        for (int i = 0; i < data.length; i++) {
            transformed[i] = data[i];
        }

        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) Math.round(transformed[i]);
        }
    }
    
    public static TargetDataLine getTargetDataLine() throws IOException, LineUnavailableException {
        List<TargetDataLine> dataLines = new ArrayList<>();
        List<Mixer> mixers = new ArrayList<>();

        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            Mixer mixer = AudioSystem.getMixer(mixerInfo);
            Line.Info[] lineInfo = mixer.getTargetLineInfo();
            for (Line.Info info : lineInfo) {
                Line l = mixer.getLine(info);
                if (l instanceof TargetDataLine) {
                    dataLines.add((TargetDataLine) l);
                    mixers.add(mixer);
                }
            }
        }
        
        System.out.println("Choose device to stream:");
        for (int i = 0; i < mixers.size(); i++) {
            Mixer mixer = mixers.get(i);
            System.out.println(i + ": " + mixer.getMixerInfo().getName());
        }
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter device > ");  
        int index = sc.nextInt();

        System.out.println("Device selected: " + mixers.get(index).getMixerInfo().getName());

        return dataLines.get(index);
    }
}
