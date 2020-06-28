package model;

import java.util.List;

public interface State {
    void reset();
    void reset(State state);
    int getScore();
    void move(int position);
    int getNumberOfMoves();
    boolean gameOver();
    State copy();
    List<Integer> getBoardElements();
    int getCentreElement();

    double getZeroAngle();

    void setZeroAngle(double radians);
}
