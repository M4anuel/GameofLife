package com.gol.GameOfLife;

import javafx.scene.paint.Color;

public enum ColorScheme {

    LIGHT("Light", Color.WHITE, Color.LIGHTGRAY, Color.BLACK, Color.LIGHTGRAY, Color.WHITE, Color.BLACK, Color.LIGHTGRAY),
    //DARCULA,
    DARK("Dark", Color.DARKGRAY.darker().darker(), Color.DARKGRAY, Color.LIGHTGRAY, Color.DARKGRAY.darker().darker(), Color.DARKGRAY.darker().darker(), Color.WHITE, Color.DARKGRAY);

    final Color canvasBackground;
    final Color menu;
    final Color tiles;
    final Color grid;
    final Color menuBackground;
    final Color menuFontColor;
    final Color menuElementColor;

    final String name;

    ColorScheme(String name, Color canvasBackground, Color menu, Color tiles, Color grid, Color menuBackground, Color menuFontColor, Color menuElementColor) {
        this.name = name;
        this.canvasBackground = canvasBackground;
        this.menu = menu;
        this.tiles = tiles;
        this.grid = grid;
        this.menuBackground = menuBackground;
        this.menuFontColor = menuFontColor;
        this.menuElementColor = menuElementColor;
    }

    public static String getColorName(Color color) { //FIXME debug this
        if (Color.WHITE.equals(color)) {
            return "WHITE";
        } else if (Color.LIGHTGRAY.equals(color)) {
            return "LIGHTGRAY";
        } else if (Color.DARKGRAY.equals(color)) {
            return "DARKGRAY";
        } else if (Color.BLACK.equals(color)) {
            return "BLACK";
        } else if (Color.DARKGRAY.darker().darker().equals(color)) {
            return "#1e1e1e";
        } else throw new RuntimeException("yalls colours wack");
    }

}
