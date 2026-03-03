package se.kth;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.annotations.QuarkusMain;

@QuarkusMain
public class Main {

    public static void main(String[] args) {
        System.out.println("MAKKA! test 42");
        Quarkus.run(args);
        System.out.println("ABA!");
    }
}