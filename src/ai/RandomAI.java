package ai;

import model.State;

public class RandomAI implements AI {
    @Override
    public int getMove(State state) {

        // This will be too quick to see without a pause
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // When placing elements onto the board, the number
        // of moves is equal to the number of elements on the
        // board.  For example, move 0 will place the new element
        // immediately counter-clockwise of the element with index
        // 0, thus becoming the new index 0.  When taking an element
        // of the board with a blue minus, or when duplicating an
        // element with a neutrino, the move refers to the index of
        // the element, so n is still equal to the number of elements
        int n = state.getNumberOfMoves();

        // If n is negative, -1 is an available move
        // This means that we have used a "blue minus"
        // to remove an element and have the option to
        // transform it into a "red plus".  I'll take
        // that option, but here's an example of how
        // you might deal with this situation.

        /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
         *   int start = 0;
         *   if (n < 0) {
         *       start = -1;
         *       n *= -1;
         *   }
         *   for (int move = start ; move < n - 1 ; move++) {
         *       // Do something with 'move'
         *   }
         * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

        // Random AI logic

        // Always change a taken element into a red plus if possible
        if (n < 0) {
            return -1;
        }
        // Pick a move at random, otherwise
        return (int)(Math.random() * n);
    }
}
