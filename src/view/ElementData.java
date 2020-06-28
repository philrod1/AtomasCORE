package view;

import java.awt.Color;

public class ElementData {
    final int id;
    final String symbol;
    final String name;
    final Color colour;
    ElementData(int id, String symbol, String name, String colour) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.colour = Color.decode(colour);
    }

    @Override
    public String toString() {
        return id + ": " + symbol + " " + name;
    }
}
