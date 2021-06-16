package com.julianrwalston;

import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        GameManager manager = new GameManager(50, 50);
        manager.setUpGame();
    }
}
