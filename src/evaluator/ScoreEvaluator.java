package evaluator;

import model.State;

public class ScoreEvaluator implements Evaluator {
    @Override
    public double evaluate(State state) {
        return state.getScore();
    }
}
