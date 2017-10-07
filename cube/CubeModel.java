package cube;

import java.util.Observable;

/** Models an instance of the Cube puzzle: a cube with color on some sides
 *  sitting on a cell of a square grid, some of whose cells are colored.
 *  Any object may register to observe this model, using the (inherited)
 *  addObserver method.  The model notifies observers whenever it is modified.
 *  @author P. N. Hilfinger
 *  @author Florence Lau
 */
class CubeModel extends Observable {

    /** A blank cube puzzle of size 4. */
    CubeModel() {
        initialize(4, 0, 0, new boolean[4][4]);
    }

    /** A copy of CUBE. */
    CubeModel(CubeModel cube) {
        initialize(cube);
    }

    /** Initialize puzzle of size SIDExSIDE with the cube initially at
     *  ROW0 and COL0, with square r, c painted iff PAINTED[r][c], and
     *  with face k painted iff FACEPAINTED[k] (see isPaintedFace).
     *  Assumes that
     *    * SIDE > 2.
     *    * PAINTED is SIDExSIDE.
     *    * 0 <= ROW0, COL0 < SIDE.
     *    * FACEPAINTED has length 6.
     */
    void initialize(int side, int row0, int col0, boolean[][] painted,
                    boolean[] facePainted) {
        size = side;
        curRow = row0;
        curCol = col0;
        squareIsPainted = painted;
        faceIsPainted = facePainted;
        setChanged();
        notifyObservers();
    }

    /** Initialize puzzle of size SIDExSIDE with the cube initially at
     *  ROW0 and COL0, with square r, c painted iff PAINTED[r][c].
     *  The cube is initially blank.
     *  Assumes that
     *    * SIDE > 2.
     *    * PAINTED is SIDExSIDE.
     *    * 0 <= ROW0, COL0 < SIDE.
     */
    void initialize(int side, int row0, int col0, boolean[][] painted) {
        initialize(side, row0, col0, painted, new boolean[6]);
    }

    /** Initialize puzzle to be a copy of CUBE. */
    void initialize(CubeModel cube) {
        this.size = cube.side();
        this.curRow = cube.cubeRow();
        this.curCol = cube.cubeCol();
        this.numMoves = cube.moves();
        this.everyFacePainted = cube.allFacesPainted();
        this.squareIsPainted = new boolean[size][size];
        this.faceIsPainted = new boolean[6];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                this.squareIsPainted[r][c] = cube.isPaintedSquare(r, c);
            }
        }
        for (int f = 0; f < 6; f++) {
            this.faceIsPainted[f] = cube.isPaintedFace(f);
        }
        setChanged();
        notifyObservers();
    }

    /** Move the cube to (ROW, COL), if that position is on the board and
     *  vertically or horizontally adjacent to the current cube position.
     *  Transfers colors as specified by the rules.
     *  Throws IllegalArgumentException if preconditions are not met.
     */
    void move(int row, int col) {
        if (row < size && col < size && row >= 0 && col >= 0
                && ((row == curRow && Math.abs(curCol - col) == 1)
                || (col == curCol && Math.abs(curRow - row) == 1))) {
            boolean[] faceIsPaintedCopy = new boolean[6];
            for (int i = 0; i < 6; i++) {
                faceIsPaintedCopy[i] = faceIsPainted[i];
            }
            if (col > curCol && row == curRow) {
                faceIsPainted[3] = faceIsPaintedCopy[5];
                faceIsPainted[5] = faceIsPaintedCopy[2];
                faceIsPainted[2] = faceIsPaintedCopy[4];
                faceIsPainted[4] = faceIsPaintedCopy[3];
            } else if (col < curCol && row == curRow) {
                faceIsPainted[4] = faceIsPaintedCopy[2];
                faceIsPainted[2] = faceIsPaintedCopy[5];
                faceIsPainted[5] = faceIsPaintedCopy[3];
                faceIsPainted[3] = faceIsPaintedCopy[4];
            } else if (col == curCol && row > curRow) {
                faceIsPainted[1] = faceIsPaintedCopy[5];
                faceIsPainted[5] = faceIsPaintedCopy[0];
                faceIsPainted[0] = faceIsPaintedCopy[4];
                faceIsPainted[4] = faceIsPaintedCopy[1];
            } else if (col == curCol && row < curRow) {
                faceIsPainted[4] = faceIsPaintedCopy[0];
                faceIsPainted[0] = faceIsPaintedCopy[5];
                faceIsPainted[5] = faceIsPaintedCopy[1];
                faceIsPainted[1] = faceIsPaintedCopy[4];
            }
            if (squareIsPainted[row][col] && !faceIsPainted[4]) {
                squareIsPainted[row][col] = false;
                faceIsPainted[4] = true;
            } else if (!squareIsPainted[row][col] && faceIsPainted[4]) {
                squareIsPainted[row][col] = true;
                faceIsPainted[4] = false;
            }
            curRow = row;
            curCol = col;
            numMoves += 1;
            setChanged();
            notifyObservers();
        } else {
            throw new IllegalArgumentException("Cannot move to this position.");
        }
    }

    /** Return the number of squares on a side. */
    int side() {
        return size;
    }

    /** Return true iff square ROW, COL is painted.
     *  Requires 0 <= ROW, COL < board size. */
    boolean isPaintedSquare(int row, int col) {
        return squareIsPainted[row][col];
    }

    /** Return current row of cube. */
    int cubeRow() {
        return curRow;
    }

    /** Return current column of cube. */
    int cubeCol() {
        return curCol;
    }

    /** Return the number of moves made on current puzzle. */
    int moves() {
        return numMoves;
    }

    /** Return true iff face #FACE, 0 <= FACE < 6, of the cube is painted.
     *  Faces are numbered as follows:
     *    0: Vertical in the direction of row 0 (nearest row to player). (front)
     *    1: Vertical in the direction of last row. (back)
     *    2: Vertical in the direction of column 0 (left column). (left)
     *    3: Vertical in the direction of last column. (right)
     *    4: Bottom face.
     *    5: Top face.
     */
    boolean isPaintedFace(int face) {
        return faceIsPainted[face];
    }

    /** Return true iff all faces are painted. */
    boolean allFacesPainted() {
        if (isPaintedFace(0) && isPaintedFace(1) && isPaintedFace(2)
                && isPaintedFace(3) && isPaintedFace(4) && isPaintedFace(5)) {
            everyFacePainted = true;
        } else {
            everyFacePainted = false;
        }
        return everyFacePainted;
    }

    /** Instantiate a variable for the current row. */
    private int curRow;
    /** Instantiate a variable for the current column. */
    private int curCol;
    /** Instantiate a variable for the size of the grid. */
    private int size;
    /** Instantiate a variable for the number of moves taken. */
    private int numMoves;
    /** Instantiate a variable for whether every face of the cube is painted. */
    private boolean everyFacePainted;
    /** Instantiate a variable for whether the square at the given row
     * and column is painted. */
    private boolean[][] squareIsPainted;
    /** Instantiate a variable for whether the face of the cube is painted. */
    private boolean[] faceIsPainted;
}
