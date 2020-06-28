package util;

import java.awt.*;

public class Constants {

    public static final String DATA_FILE = "res/AtomData.txt";

    //Atoms constants
    public static final int N_START_ATOMS = 6;
    public static final int N_MAX_ATOMS = 19;
    public static final int MAX_MOVES_NO_PLUS = 5;
    public static final int N_MOVES_NO_MINUS = 20;
    public static final int N_CRITICAL_ELEMENTS = 12;
    public static final int REACTION_BONUS = 50;

    public static final int NEUTRINO = -3;
    public static final int DARK_PLUS = -2;
    public static final int MINUS = -1;
    public static final int PLUS = 0;

    //Atoms probabilities
    public static final double PLUS_PROBABILITY       = 0.1666666666666667;
    public static final double DARK_PLUS_PROBABILITY  = 0.0111111111111111;
    public static final double NEUTRINO_PROBABILITY   = 0.0166666666666667;

    // Game moves constants
    public static final double BASE_INCREASE_MOVES    = 45;
    public static final double BOUND_INCREASE_MOVES   = 450;
    public static final double NEUTRINO_MIN_SCORE     = 1500;
    public static final double DARK_PLUS_MIN_SCORE    = 750;
    public static final int MAX_ATOM_VALUE            = 125;

    //Fonts
    public static final Font ATOM_NAME_FONT = new Font("MONOSPACED", Font.BOLD, 18);
    public static final Font ATOM_VALUE_FONT = new Font("MONOSPACED", Font.BOLD, 10);
    public static final Font SCORE_FONT = new Font("MONOSPACED", Font.BOLD, 35);
    public static final Font TOP_ATOM_FONT = new Font("MONOSPACED", Font.BOLD, 25);
    public static final Font CONTROL_MENU_FONT = new Font("MONOSPACED", Font.BOLD, 20);

    //Colour
    public static final Color BACKGROUND_COLOUR = new Color(0x4a292c);

}
