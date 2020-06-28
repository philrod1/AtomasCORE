package view;

import model.State;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.io.FileReader;
import java.util.*;
import java.util.List;

import static util.Constants.*;

public class GamePanel extends JPanel {

    private static final Map<Integer, ElementData> ELEMENT_DATA = new TreeMap<>();
    private static final double[] THETAS = new double[20];
    static {
        init();
    }

    private final State state;

    private final List<Area> elementShapes = new ArrayList<>(20);
    private final List<Area> spaceShapes = new ArrayList<>(20);
    private final double[] elementAngles = new double[20];
    private final double[] spaceAngles = new double[20];
    private final JButton undo;
    private final JButton redo;
    private final JButton pause;
    private Area centreArea;

    private int highElement = 0;

    public GamePanel(State state, JButton undo, JButton redo, JButton pause) {
        this.state = state;
        this.undo = undo;
        this.redo = redo;
        this.pause = pause;
        add(undo);
        add(pause);
        add(redo);
        this.setBackground(BACKGROUND_COLOUR);
    }

    @Override
    public void paintComponent(Graphics g) {
        elementShapes.clear();
        spaceShapes.clear();
        super.paintComponent(g);
        double w = getWidth();
        double eRadius = w / 20;
        double oRadius = w / 3;
        List<Integer> elements = state.getBoardElements();
        double dist = THETAS[elements.size()];
        int centerElement = state.getCentreElement();
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,  RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Point origin = new Point(getWidth()/2, getHeight()/2 + 60);
        ElementData element = ELEMENT_DATA.get(centerElement);
        g2.setColor(element.colour);
        g2.fillOval((int)(origin.x - eRadius), (int)(origin.y - eRadius), (int)(eRadius * 2), (int)(eRadius * 2));
        centreArea = new Area(new Ellipse2D.Double(origin.x - eRadius, origin.y - eRadius, eRadius * 2, eRadius * 2));
        labelAtom(g2, element, origin.x, origin.y, (int)eRadius, -1);

        double theta = state.getZeroAngle();
        for (int i = 0 ; i < elements.size() ; i++) {
            if (element.id > highElement) {
                highElement = element.id;
            }
            element = ELEMENT_DATA.get(elements.get(i));
            double x = Math.cos(theta) * oRadius + origin.x;
            double y = Math.sin(theta) * oRadius + origin.y;
            g2.setColor(element.colour);
            g2.fillOval((int)(x - eRadius), (int)(y - eRadius), (int)(eRadius * 2), (int)(eRadius * 2));
            elementShapes.add(new Area(new Ellipse2D.Double(x - eRadius, y - eRadius, eRadius * 2, eRadius * 2)));
            labelAtom(g2, element, (int)x, (int)y, (int)eRadius, i);
            elementAngles[i] = theta - dist;
            theta += dist/2;

            x = Math.cos(theta) * oRadius + origin.x;
            y = Math.sin(theta) * oRadius + origin.y;
            g2.setColor(new Color(0x0AFFFFFF, true));
            g2.drawOval((int)(x - eRadius), (int)(y - eRadius), (int)(eRadius * 2), (int)(eRadius * 2));
            spaceShapes.add(new Area(new Ellipse2D.Double(x - eRadius, y - eRadius, eRadius * 2, eRadius * 2)));
            spaceAngles[i] = theta - dist;

            theta += dist/2;
        }

        g2.setColor(Color.WHITE);
        g2.setFont(SCORE_FONT);
        FontMetrics metrics = g2.getFontMetrics(SCORE_FONT);
        String score = Integer.toString(state.getScore());
        int x = origin.x - metrics.stringWidth(score) / 2;
        int y = origin.y - (int)oRadius - 100;
        g2.drawString(score, x, y);

        g2.setFont(TOP_ATOM_FONT);
        metrics = g2.getFontMetrics(TOP_ATOM_FONT);
        String name = ELEMENT_DATA.get(highElement).name;
//        System.out.println(highElement + " " + name);
        x = origin.x - metrics.stringWidth(name) / 2;
        y = origin.y - (int)oRadius - 70;
        g2.drawString(name, x, y);
        undo.repaint();
        pause.repaint();
        redo.repaint();
        g2.dispose();
        g.dispose();
    }

    /**
     * Label atom -- Thanks, Iliyana!
     * @param g2d - graphics
     * @param element - element to be labelled
     * @param a - width starting point depending on the board's width and element's radius
     * @param b - height starting point depending on the board's height and element's radius
     */
    private void labelAtom(Graphics2D g2d, ElementData element, int a, int b, int radius, int index) {
        g2d.setColor(Color.WHITE);
        int diameter = 2 * radius;
        //Draw element's name
        String symbol = element.symbol;
        FontMetrics metrics = g2d.getFontMetrics(ATOM_NAME_FONT);
        int x = a - radius + (diameter - metrics.stringWidth(symbol)) / 2;
        int y = b - radius + ((diameter - metrics.getHeight()) / 2) + metrics.getAscent() - 2;
        g2d.setFont(ATOM_NAME_FONT);
        g2d.drawString(symbol, x, y);

        if (element.id < 1) {
            return;
        }

        //Draw element's value
        String value = String.valueOf(element.id);
        metrics = g2d.getFontMetrics(ATOM_VALUE_FONT);
        x = a - radius + (diameter - metrics.stringWidth(value)) / 2;
        y = b - radius + 10 + ((diameter - metrics.getHeight()) / 2) + metrics.getAscent();
        g2d.setFont(ATOM_VALUE_FONT);
        g2d.drawString(value, x, y);

        if (index >= 0) {
            value = String.valueOf(index);
            x = a - radius + (diameter - metrics.stringWidth(value)) / 2;
            y = b - radius - 15 + ((diameter - metrics.getHeight()) / 2) + metrics.getAscent();
//            g2d.setFont(ATOM_VALUE_FONT);
            g2d.drawString(value, x, y);
        }

    }


    private static void init() {
        ELEMENT_DATA.clear();
        try {
            Scanner sc = new Scanner(new FileReader(DATA_FILE));
            while (sc.hasNext()) {
                String[] line = sc.nextLine().split("\t");
                int id = Integer.parseInt(line[0].trim());
                String symbol = line[1].trim();
                String name   = line[2].trim();
                String colour = line[3].trim();
                ElementData element = new ElementData(id, symbol, name, colour);
//                System.out.println(element);
                ELEMENT_DATA.put(id, element);
            }
            sc.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Data file is missing or corrupted.");
        }
        double c = 2 * Math.PI;
        THETAS[0] = 0;
        for (int i = 1 ; i < 20 ; i++) {
            THETAS[i] = c / i;
        }
    }

    public void makeMove(int move) {
        if (move == -1) {
            repaint();
            return;
        }
        int size = state.getBoardElements().size();
        double ref = getReferenceAngle(move);
        double dist = THETAS[size];
        state.setZeroAngle((ref - dist * move) % (2 * Math.PI));
        repaint();
    }

    private double getReferenceAngle(int move) {
        int size = state.getBoardElements().size();
        move = (move + size) % size;
        int centerElement = state.getCentreElement();
        if (centerElement == -1 || centerElement == -3) {
            return elementAngles[move];
        }
        return spaceAngles[move];
    }

    public int getElementIndex(Point p) {
        if (centreArea.contains(p)) {
            return -1;
        }
        int index = -2;
        for (int i = 0 ; i < elementShapes.size() ; i++) {
            Area a = elementShapes.get(i);
            if (a.contains(p)) {
                index = i;
                break;
            }
        }
        return index;
    }

    public int getSpaceIndex(Point p) {
        int index = -2;
        int size = spaceShapes.size();
        for (int i = 0 ; i < size ; i++) {
            Area a = spaceShapes.get(i);
            if (a.contains(p)) {
                index = i;
                break;
            }
        }
        return (index + 1) % size;
    }
}
