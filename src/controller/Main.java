package controller;

import ai.AI;
import ai.RandomAI;
import model.BasicState;
import model.State;
import view.GamePanel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Scanner;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {

        final AI ai = new RandomAI();
        final boolean auto = true;

        final int[] clickIndices = new int[] {-2, -2};
        final BasicState game = new BasicState();
        final boolean[] paused = new boolean[] {false};

        final Stack<State> undoStack = new Stack<>();
        final Stack<State> redoStack = new Stack<>();

        undoStack.push(game.copy());

        JButton undo = new JButton("Undo");
        JButton redo = new JButton("Redo");
        JButton pause = new JButton("Play/Pause");

        final GamePanel gp = new GamePanel(game, undo, redo, pause);

        undo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!undoStack.isEmpty()) {
                    State undo = undoStack.pop();
                    redoStack.push(undo);
                    game.reset(undo);
                    gp.repaint();
                }
            }
        });
        redo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (!redoStack.isEmpty()) {
                    State redo = redoStack.pop();
                    undoStack.push(redo);
                    game.reset(redo);
                    gp.repaint();
                }
            }
        });
        pause.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                paused[0] = !paused[0];
            }
        });

        gp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                clickIndices[0] = gp.getSpaceIndex(e.getPoint());
                clickIndices[1] = gp.getElementIndex(e.getPoint());
            }
        });
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Puzzle");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setBounds(100, 100, 500, 600);
            frame.add(gp);
            frame.setVisible(true);
            frame.setResizable(false);
        });
        if (auto) {
            while (!game.gameOver()) {
                System.out.println(game);
                int move = ai.getMove(game);
                if (!paused[0]) {
                    game.move(move);
                    undoStack.push(game.copy());
                    gp.makeMove(move);
                }
                while (paused[0]) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Scanner scanner = new Scanner(System.in);
            while (!game.gameOver()) {
//                System.out.println(game);
//                System.out.print("Move? ");
//                int move = scanner.nextInt();
                if (game.getCentreElement() == -3 || game.getCentreElement() == -1) {
                    while (clickIndices[1] < -1) {
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    game.move(clickIndices[1]);
                    undoStack.push(game.copy());
                    gp.makeMove(clickIndices[1]);
                } else {
                    while (clickIndices[0] < -1) {
                        try {
                            Thread.sleep(2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    game.move(clickIndices[0]);
                    undoStack.push(game.copy());
                    gp.makeMove(clickIndices[0]);
                }

                clickIndices[1] = -2;
                clickIndices[0] = -2;
                System.out.println(game);
//                gp.repaint();
//                gp.focusOn(game.getFocusElement());
            }
        }
        System.out.println("GAME OVER");
        System.out.println(game);
    }
}
