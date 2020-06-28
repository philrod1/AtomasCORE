package model;

import java.util.*;

import static util.Constants.*;

public class BasicState implements State {

    private static final Random RNG = new Random();
    private double zeroPosition = 0;  // Position in radians of the first element.  For the view but put here for undo/redo
    private int centreElement = 0;
    private final List<Integer> elements;

    // The placement of the transformed Minus Atom to Plus Atom on the board does not count?
    private int moveCounter;

    // Plus atom - every 6th move at worst case scenario.
    private int plusCounter;

    private int score;
    private int baseValue;
    private int bound;
    private boolean minusFlag;

    public BasicState() {
        elements = new LinkedList<>();
        reset();
//        score = 1600;
//        elements.clear();
//        elements.addAll(Arrays.asList(2,1,1,2));
//        centreElement = 0;
    }

    @Override
    public void reset(State old) {
        BasicState state = (BasicState) old;
        score = state.score;
        centreElement = state.centreElement;
        elements.clear();
        elements.addAll(state.elements);
        minusFlag = state.minusFlag;
        moveCounter = state.moveCounter;
        plusCounter = state.plusCounter;
        baseValue = state.baseValue;
        bound = state.bound;
        zeroPosition = state.zeroPosition;
    }

    @Override
    public void reset() {
        score = 0;
        moveCounter = 0;
        baseValue = 1;
        bound = 3;
        minusFlag = false;
        elements.clear();
        for (int i = 0; i < N_START_ATOMS; i++) {
            elements.add(RNG.nextInt(bound) + baseValue);
        }
        centreElement = getRandomElement();
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public void move(int position) {
        if (position == -1) {
            if (minusFlag) {
                minusFlag = false;
                centreElement = 0;
                //TODO: moveCounter++?
            }
            return;
        }
        if (position > elements.size()) {
            System.err.println("Move out of bounds: " + position + " " + elements.size());
            return;
        }
        minusFlag = false;
        if (centreElement == NEUTRINO) {
            centreElement = elements.get(position);
        } else if (centreElement == MINUS) {
            centreElement = elements.remove(position);
            collide();
            minusFlag = true;
        } else {
            elements.add(position, centreElement);
            centreElement = getRandomElement();
            collide();
        }
        moveCounter++;
        plusCounter++;
        if (moveCounter % BASE_INCREASE_MOVES == 0) {
            baseValue++;
        }
        if (moveCounter % BOUND_INCREASE_MOVES == 0) {
            bound++;
        }
    }

    @Override
    public int getNumberOfMoves() {
        if (elements.size() > 18) {
            return 0;
        }
        int moves = elements.size() + 1;
        return minusFlag ? -moves : moves;
    }

    @Override
    public boolean gameOver() {
        return elements.size() > 18;
    }

    @Override
    public State copy() {
        BasicState copy = new BasicState();
        copy.score = score;
        copy.centreElement = centreElement;
        copy.elements.clear();
        copy.elements.addAll(elements);
        copy.minusFlag = minusFlag;
        copy.moveCounter = moveCounter;
        copy.plusCounter = plusCounter;
        copy.baseValue = baseValue;
        copy.bound = bound;
        copy.zeroPosition = zeroPosition;
        return copy;
    }

    @Override
    public List<Integer> getBoardElements() {
        return List.copyOf(elements);
    }

    @Override
    public int getCentreElement() {
        return centreElement;
    }

    @Override
    public double getZeroAngle() {
        return zeroPosition;
    }

    @Override
    public void setZeroAngle(double radians) {
        zeroPosition = radians;
    }

    private int getRandomElement() {

        // Check if Minus Atom must be generated
        if ((moveCounter - 2) % N_MOVES_NO_MINUS == 0) {
            return -1;
        }

        // Check if Plus Atom must be generated
        if (plusCounter >= MAX_MOVES_NO_PLUS) {
            plusCounter = 0;
            return 0;
        }

        double p = Math.random();

        // Put Dark Plus chance 1/90 if score > 750
        if (score > DARK_PLUS_MIN_SCORE) {
            if (p < DARK_PLUS_PROBABILITY) {
                return -2;
            }
        }
        p -= DARK_PLUS_PROBABILITY;

        // Put Neutrino chance 1/60 if score > 1500
        if (score > NEUTRINO_MIN_SCORE) {
            if (p < NEUTRINO_PROBABILITY) {
                return -3;
            }
        }
        p -= NEUTRINO_PROBABILITY;

        // 1 in 6 chance of a plus
        if (p < PLUS_PROBABILITY) {
            plusCounter = 0;
            return 0;
        }

        p = Math.random();

        //TODO: Deal with lower elements.  Does this make sense?
        // (number of lower atoms on the board )/(number of all atoms on the board)* (1/3)
        for (int element : elements) {
            if (element > 0
                    && element < baseValue
                    && p < (1.0 / (elements.size() * 3))) {
                return element;     // Seems a tad rude to return mid-iteration :/
            }
        }

        // Regular atoms have equal chance of being spawned
        double share = 1.0 / bound;
        for (int i = 1 ; i < bound ; i++) {
            if (p < i*share) {
                return baseValue + (i-1);
            }
        }

        return baseValue + bound - 1;
    }

    private void collide() {
        collide(0, -100, -100);
    }

    private void collide(int chainLength, int newElement, int plusIndex) {
        int nElements = elements.size();
        boolean finished = true;
        int i = Math.max(plusIndex, 0);
        while (i < nElements) {
            boolean darkPlus = elements.get(i) == DARK_PLUS;
            if (darkPlus || elements.get(i) == PLUS) {
                int plus = i;
                int right = (plus + 1) % nElements;
                int left = (plus + nElements - 1) % nElements;
                if (left == right) {
                    break;
                }
                if (left > right) {
                    int temp = left;
                    left = right;
                    right = temp;
                }
                int leftValue = elements.get(left);
                int rightValue = elements.get(right);
                boolean willFuse = darkPlus || (leftValue == rightValue && leftValue != PLUS);
                if (willFuse) {
                    int centreValue = newElement;
                    finished = false;
                    newElement = Math.max(newElement, leftValue) + ((chainLength == 1 && leftValue >= newElement) ? 2 : 1);
                    if (darkPlus) {
                        newElement = (leftValue == PLUS) ? 4 : Math.max(leftValue, rightValue) + 3;
                        elements.remove(plus);
                        elements.add(plus, 0);
                    }
                    chainLength++;
                    score += calculateFuseScore(darkPlus, centreValue, leftValue, rightValue, chainLength);
                    try {
                        elements.remove(right);
                        elements.remove(left);
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println(this);
                        System.out.println("Chain length: " + chainLength + ", Left: " + left + ", Right: " + right);
                        System.exit(1);
                    }
                    int diff = -1;
                    if (left > plus) {
                        diff = 0;
                    } else if (right < plus) {
                        diff = -2;
                    }
                    plus = (plus + diff + elements.size()) % elements.size();
                    collide(chainLength, newElement, plus);
                    break;
                } else if (plusIndex == i) {
                    break;
                }
            }
            i++;
        }
        if (finished && plusIndex > -100) {
            try {
                elements.remove(plusIndex);
                elements.add(plusIndex, newElement);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(this);
                System.out.println("Chain length: " + chainLength + ", plus index: " + plusIndex);
                System.exit(1);
            }
            collide();
        }
    }

    private int calculateFuseScore(boolean darkPlus, int centreValue, int leftValue, int rightValue, int chainLength) {
        /*
         *  Score Calculation:
         *
         *  Simple reaction: ( if r == 1 )
         *  Score = floor(1.5 * (Z + 1))
         *
         *  Chain reaction:
         *  M = 1 + 0.5 * r             where r is the rth reaction
         *  Sr = floor(M * (Z + 1))
         *  B = 2 * M * (Zo - Z + 1)    where Zo is the outer atom value && Z is the middle atom
         *
         *  score = Sr               if Zo < Z
         *  score = Sr + B           if Zo >= Z
         *
         *  If a Dark Plus is used:
         *  Score = (Atom1Value + Atom2Value) / 2
         *
         *  If there is a chain with >= 4 elements on a side:
         *  Creates an element with bonus value.
         */
        if (darkPlus) {
            return (leftValue + rightValue) / 2;
        }
        if (chainLength == 1) {
            return ((leftValue + 1) * 3) / 2;
        }
        double m = 0.5 * chainLength + 1;
        int s = (int)(m * (centreValue + 1));
        int b = (int)(2 * m * (leftValue - centreValue + 1));

        //TODO: Work out the bonus system
        // If there is a chain with >= 4 elements on a side:
        // Creates an element with bonus value.

        return (leftValue < centreValue) ? s : s + b;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
//        sb.append('(');
//        sb.append(centreElement);
//        sb.append(") : [");
//        for (int i = 0 ; i < elements.size()-1 ; i++) {
//            sb.append(elements.get(i));
//            sb.append(", ");
//        }
//        if (elements.size() > 0) {
//            sb.append(elements.get(elements.size()-1));
//        }
//        sb.append("] : ");
//        sb.append(score);
        sb.append(baseValue);
        sb.append(": [");
        for (int i = 0 ; i < bound-1 ; i++) {
            sb.append(baseValue + i);
            sb.append(", ");
        }
        sb.append(baseValue + bound - 1);
        sb.append("] : [");
        for (int i = 0 ; i < elements.size()-1 ; i++) {
            sb.append(elements.get(i));
            sb.append(", ");
        }
        if (elements.size() > 0) {
            sb.append(elements.get(elements.size()-1));
        }
        sb.append("] : ");
        return sb.toString();
    }
}
