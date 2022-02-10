package com.gol.GameOfLife;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static com.gol.GameOfLife.AnsiColour.*;

public class PresetGenerator {

    String path = "GOL-core/resources/presets.xml";
    ArrayList<Preset> presets = new ArrayList<>();
    XStream xstream = new XStream(new DomDriver());

    public void serialisePresets() throws IOException {
        //xstream.alias("Preset", Preset.class); TODO dis shit wack
        FileWriter writer = new FileWriter(path);
        writer.write(xstream.toXML(presets));
        writer.close();
    }

    public void serialisePresets(String path) throws IOException {
        //xstream.alias("preset", Preset.class); TODO dis shit wack too
        FileWriter writer = new FileWriter(path);
        writer.write(xstream.toXML(presets));
        writer.close();
    }

    public ArrayList<Preset> deserialisePresets() throws FileNotFoundException {
        //try {
            xstream.allowTypes(new Class[] {com.gol.GameOfLife.Preset.class});
            Scanner scanner = new Scanner(new File(path));
            ArrayList<Preset> presets = null;

        //presets = (ArrayList<Preset>) xstream.fromXML(scanner.useDelimiter("\\Z").next());
        //System.out.println(Arrays.deepToString(presets.get(0).presetState));

            try {
                presets = (ArrayList<Preset>) xstream.fromXML(scanner.useDelimiter("\\Z").next());
            } catch (Exception e) {
                System.out.println(ANSI_RED + "Caught exception: XML parse failed" + ANSI_RESET);
            }

            scanner.close();
            xstream.denyTypes(new Class[] {com.gol.GameOfLife.Preset.class});

            return presets;
        /*} catch (Exception e) {
            System.out.println(ANSI_RED + "Caught IOException: invalid generator path" + ANSI_RESET);
            return null;
        }*/
    }

    public void addPreset(Preset preset) {
        presets.add(preset);
    }

    public void removePreset(Preset preset) {
        presets.remove(preset);
    }

    public void removePreset(int index) {
        presets.remove(index);
    }

    public void clearPresets() {
        presets.clear();
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Preset getPreset(String name) {
        for (Preset preset : presets) {
            if (preset.name.equals(name)) {
                return preset;
            }
        }

        System.out.println("Could not find preset");
        return null;
    }

    public Preset getPreset(int index) {
        return presets.get(index);
    }

    public boolean isOscillator(Preset preset) {
        return preset.oscillator;
    }

    public boolean isGun(Preset preset) {
        return preset.gun;
    }

}

class Preset {

    //TODO perhaps add default window size and integrate stuff

    String name;
    boolean[][] presetState;
    int minReqWindowWidth = 0;
    int minReqWindowHeight = 0;
    boolean oscillator = false;
    boolean gun = false;

    public Preset(String name, boolean[][] presetState) {
        this.name = name;
        this.presetState = presetState;
        this.minReqWindowWidth = presetState[0].length;
        this.minReqWindowHeight = presetState.length;
    }

    public Preset(String name, boolean[][] presetState, int minReqWindowWidth, int minReqWindowHeight, boolean oscillator, boolean gun) {
        this.name = name;
        this.presetState = presetState;
        this.minReqWindowWidth = minReqWindowWidth;
        this.minReqWindowHeight = minReqWindowHeight;
        this.oscillator = oscillator;
        this.gun = gun;
    }

}

class Test {

    public static boolean[][] cubli = {
            {true, true},
            {true, true}
    };

    public static boolean[][] stic = {
            {true, true, true}
    };

    public static void main(String[] args) throws IOException {
        PresetGenerator generator = new PresetGenerator();
        generator.addPreset(new Preset("cubli", cubli));
        generator.addPreset(new Preset("stic", stic));
        generator.serialisePresets();
        ArrayList<Preset> dud = (ArrayList<Preset>) generator.deserialisePresets();
        System.out.println(Arrays.deepToString(dud.get(0).presetState));
    }

}
