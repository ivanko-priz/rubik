package rubik;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class Cube {
    private char[] cells;
    private final char[] colors = {'r', 'g', 'b', 'w', 'o', 'y'};
    private final int size;
    private final int sizeSqrt;
    private Map<Integer, EnumMap<SideDirection, Integer>> edges;

    Cube() {
        this.size = 9;
        this.sizeSqrt = (int) Math.sqrt(this.size);
        this.cells = new char[54];
        this.edges = new HashMap<>();

        for (int i = 0; i < this.cells.length; i++) {
            this.cells[i] = this.colors[i / 9];
        }

        this.setEdges();
    }

    private void setEdges() {
        if (this.edges.size() == 0) {
            EnumMap<SideDirection, Integer> m;

            m = new EnumMap<>(SideDirection.class);
            m.put(SideDirection.TOP, 1);
            m.put(SideDirection.RIGHT, 2);
            this.edges.put(0, m.clone());

            m = new EnumMap<>(SideDirection.class);
            m.put(SideDirection.RIGHT, 0);
            m.put(SideDirection.TOP, 2);
            this.edges.put(1, m.clone());

            m = new EnumMap<>(SideDirection.class);
            m.put(SideDirection.TOP, 0);
            m.put(SideDirection.RIGHT, 1);
            this.edges.put(2, m.clone());
        }
    }


    private int getAdjacentSide(int currentSide, SideDirection d) {
        EnumMap<SideDirection, Integer> s;
        int adjacentSide;

        if (this.edges.containsKey(currentSide)) {
            s = this.edges.get(currentSide);

            if (s.containsKey(d)) {
                adjacentSide = s.get(d);
            } else  {
                if (!s.containsKey(d.getOpposite())) {
                    adjacentSide = this.getAdjacentSide(this.getOppositeSide(currentSide), d.getOpposite());
                } else {
                    adjacentSide = this.getOppositeSide(s.get(d.getOpposite()));
                }

                s.put(d, adjacentSide);

                this.edges.put(currentSide, s);
            }
        } else {
            adjacentSide = this.getAdjacentSide(this.getOppositeSide(currentSide), d.getOpposite());
            EnumMap<SideDirection, Integer> m = new EnumMap<>(SideDirection.class);
            m.put(d, adjacentSide);

            this.edges.put(currentSide, m);
        }

        return adjacentSide;
    }

    private int getOppositeSide(int s) {
        return 5 - s;
    }

    // assumes clockWise is always true and layer = 0;
    public void rotate(int side, int layer, int n, boolean clockWise) throws Exception {
        if (side < 0 || side > 5) throw new Exception("Side must be between 0 and 5");
        if (layer < 0 || layer >= Math.sqrt(this.size)) throw new Exception("Layer must be in range");
        if (n <= 0) throw new Exception("Number of rotations must be greater than 0");

        int fullRotations = n % 4;

        if (fullRotations != 0) {
            if (layer == this.sizeSqrt - 1) {
                rotate(getOppositeSide(side), 0, n, !clockWise);
            } else {
                if (!clockWise) {
                    fullRotations = 4 - fullRotations;
                }

                if (layer == 0) {
                    // rotate side
                    rotateClockWise(side, fullRotations);
                }

                // rotate adjacent sides
                rotateAdjacentSidesClockwise(side, fullRotations, layer);
            }
        }

    }

    private void rotateAdjacentSidesClockwise(int side, int n, int layer) {
        SideDirection[] directions = {
            SideDirection.LEFT,
            SideDirection.TOP,
            SideDirection.RIGHT,
            SideDirection.BOTTOM,
        };
        int[] sideIndeces;
        int[] indeces = new int[this.sizeSqrt * directions.length];
        char[] shiftedValues = new char[this.sizeSqrt * directions.length];

        for (int i = 0; i < directions.length; i++) {
            sideIndeces = getSideIndeces(getAdjacentSide(side, directions[i]), directions[i].getAdjacent(), layer);

            for (int j = 0; j < this.sizeSqrt; j++) {
                indeces[i * this.sizeSqrt + j] = sideIndeces[j];
            }
        }

        // shift values
        for (int i = 0; i < indeces.length; i++) {
            int shiftedIndex = n * this.sizeSqrt + i;

            if (shiftedIndex >= indeces.length) {
                shiftedIndex = shiftedIndex % (indeces.length);
            }

            shiftedValues[i] = this.cells[indeces[shiftedIndex]];
        }

        for (int i = 0; i < indeces.length; i++) {
            this.cells[indeces[i]] = shiftedValues[i];
        }
    }

    private int[] getSideIndeces(int side, SideDirection d, int layer) {
        int[] indeces = new int[this.sizeSqrt];

        int from, step;

        from = this.size * side;

        if (d == SideDirection.TOP || d == SideDirection.BOTTOM) {
            step = 1;

            if (d == SideDirection.TOP) {
                from += this.sizeSqrt;
            } else {
                from -= this.sizeSqrt;
            }
        } else {
            step = this.sizeSqrt;
        }

        if (d == SideDirection.RIGHT) {
            from += this.sizeSqrt - 1 - layer;
        } else if (d == SideDirection.BOTTOM) {
            from += this.sizeSqrt * (this.sizeSqrt - 1) + layer;
        }

        for (int i = 0; i < this.sizeSqrt; i++) {
            indeces[i] = from;
            from += step;
        }

        return indeces;
    }

    private void rotateClockWise(int side, int n) {
        int k, idx;
        char[] modified;

        k = 0;
        modified = new char[this.size];

        for (int i = 0; i < this.sizeSqrt; i++) {
            for (int j = this.sizeSqrt - 1; j >= 0; j--) {
                switch(n) {
                    case 1:
                      idx = j * this.sizeSqrt + i;
                      break;
                    case 2:
                        idx = ((this.sizeSqrt - 1) - i) * this.sizeSqrt + j;
                      break;
                    default:
                      idx = (this.sizeSqrt - 1 - j) * this.sizeSqrt + (this.sizeSqrt - 1 - i);
                      break;
                }

                modified[k] = this.cells[side * this.size + idx];
                k++;
            }
        }

        for (int i = 0; i < modified.length; i++) {
            this.cells[side * this.size + i] = modified[i];
        }
    }

    public void shuffle() {}

    public boolean isAssembled() {
        char c = this.cells[0];

        for (int i = 0; i < this.cells.length; i++) {
            if (i % this.size == 0) {
                c = this.cells[i];
            } else if (c != this.cells[i]) return false;
        }

        return true;
    }

    public void show() {
        for (int i = 0; i < this.cells.length; i++) {
            if (i % this.size == 0) {
                System.out.println("\n" + i / this.size + ":");
            }

            if (i % this.sizeSqrt == 0 && i != 0) {
                System.out.println();
            }

            System.out.print(this.cells[i]);
            System.out.print(' ');
        }

        System.out.println();
    }

    public char[] getCells() {
        return this.cells;
    }

}
