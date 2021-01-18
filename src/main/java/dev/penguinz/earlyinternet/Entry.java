package dev.penguinz.earlyinternet;

import dev.penguinz.Sylk.ApplicationBuilder;

public class Entry {

    public static void main(String[] args) {
        new ApplicationBuilder()
                .withLayers(new MainMenu())
                .setTitle("Early Internet")
                .setResizable(true)
                .setIcon("bug.png")
                .buildAndRun();
    }

}
