package com.gol.Test;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class XStreamTest {

    /*Sort of spaghetti code-ish, since both writing and reading tests are in the main method instead of separate files,
    * but fight me if you care enough.*/

    public static void main(String[] args) throws IOException {

        //Writing: create two cars from Car class below, retrieve target file, parse to XML and write to target file
        //ArrayList<Car> cars = new ArrayList<>();
        //cars.add(new Car("Fiat"/*, 100.5F, 3, new boolean[][] {{true, false}, {false, false}}*/));
        //cars.add(new Car("Mercedes", 9999.2F, 20, new boolean[][] {{false, false, true, true}, {false, true}}));

        File file = new File("GOL-core/src/com/gol/Test/cr.xml");
        //FileWriter writer = new FileWriter(file);

        XStream xstream = new XStream(new DomDriver()); //TODO look up resource efficiency and speed of other parsers

        //xstream.alias("car", Car.class);

        //writer.write(xstream.toXML(cars));
        //writer.close(); //IMPORTANT this close thingy is the devil incarnate, do not forget about it

        //Read (there is a lot to this, so comments are along the way)
        /*Natively, XStream only allows parsing primitive types and some others from XML, special permissions have to
        * be given to parse anything else. Understandably, these permissions should be kept as tight as possible, like
        * here, where only the specific type of class which is defined by this very package in this very module is
        * allowed in addition to natives.*/
        xstream.allowTypes(new Class[] {com.gol.Test.Car.class});

        Scanner scanner = new Scanner(file); //More ease-of-use than the FileReader could provide, so yeah

        /*This is the main conversion part, everything which follows after is only a further-strung example. As can be
        * seen, casting the output this way will, in the majority of cases, produce something which is so unfathomably
        * close to a hard warning it might as well be one. XStream works in mysterious ways. Not really. I just cannot
        * be asked to write more stable code, and so can literally no one else, it seems. As if that was not enough,
        * that one line also applies an evil and twisted way to read an entire file using the scanner, compacting that
        * task up into one inline piece of code, at the cost of heaps of stability and reliability. Yes, \Z is the
        * end-of-file limiter. No, I do not give a darn whether it was meant to be used this way. The try/catch also is
        * not even necessary, but you know, why not. This is not production code.*/
        try {
            ArrayList<Car> demcars = (ArrayList<Car>) xstream.fromXML(scanner.useDelimiter("\\Z").next());

            Car fiat = null;
            for (Car car : demcars) { //Search for object based on one attribute, using an advanced™ for-loop™
                if (Objects.equals(car.brand, "Fiat")) {
                    fiat = car;
                    break;
                }
            }

            if (fiat != null) { //Brint if found
                System.out.println(fiat.brand + " " + fiat.price + " " + fiat.gears + " " + Arrays.deepToString(fiat.array) + " " + fiat.test);
            }
        } catch (Exception e) {
            System.out.println("parsing failed coz yalls objects wack af");
        }

    }

}

class Car {

    //Example class

    public String brand;
    public float price;
    public int gears;
    public boolean[][] array;
    public boolean test;

    public Car(String brand) {
        this.brand = brand;
    }

    public Car(String brand, float price, int gears, boolean[][] array) {
        this.brand = brand;
        this.price = price;
        this.gears = gears;
        this.array = array;
    }

}
