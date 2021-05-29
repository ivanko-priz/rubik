package rubik;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

public class CubeTest {
    private Cube c;
    private final String SIDE_OUT_OF_RANGE_EXCEPTION_MSG = "Side must be between 0 and 5";
    private final String LAYER_OUT_OF_RANGE_EXCEPTION_MSG = "Layer must be in range";
    private final String ROTATIONS_NEGATIVE_EXCEPTION_MSG = "Number of rotations must be greater than 0";

    @BeforeEach
    public void createCube() {
        c = new Cube();
    }

    @Test
    public void testIsAssembledReturnsTrueOnFreshInstance() {
        assertTrue(c.isAssembled());
    }

    @Test
    public void testIsAssembledReturnsFalseAfterNotFullRotation() throws Exception {
        int side = 0;
        int layer = 0;
        int n = 1;
        boolean clockWise = true;

        c.rotate(side, layer, n, clockWise);
        assertFalse(c.isAssembled());
    }

    @Test
    public void testIsAssembledReturnsTrueAfterOneFullRotation() throws Exception {
        int side = 0;
        int layer = 0;
        int n = 4;
        boolean clockWise = true;

        c.rotate(side, layer, n, clockWise);
        assertTrue(c.isAssembled());
    }

    @Test
    public void testRotateShouldAssembleByPerformingOperationsBackwards() throws Exception {
        Random r = new Random();
        int[][] history = new int[100][4];

        int side;
        int layer;
        int n;
        boolean clockWise;

        for (int i = 0; i < history.length; i++) {
            side = r.nextInt(6);
            layer = r.nextInt(3);
            n = r.nextInt(4) + 1;
            clockWise = r.nextBoolean();

            c.rotate(side, layer, n, clockWise);

            history[i][0] = side;
            history[i][1] = layer;
            history[i][2] = n;
            history[i][3] = clockWise ? 1 : 0; 
        }

        assertFalse(c.isAssembled());

        for (int i = history.length - 1; i >= 0; i--) {
            side = history[i][0];
            layer = history[i][1];
            n = history[i][2];
            clockWise = history[i][3] == 1;

            c.rotate(side, layer, n, !clockWise);
        }

        assertTrue(c.isAssembled());
    }

    @Test
    public void testRotateShouldTrowIfWrongArgsPassed() {
        int side;
        int layer;
        int n;
        boolean clockWise = true;
    
        try {
            side = 100;
            layer = 0;
            n = 2;
            
            c.rotate(side, layer, n, clockWise);
        } catch (Exception e) {
            assertEquals(SIDE_OUT_OF_RANGE_EXCEPTION_MSG, e.getMessage());
        }

        try {
            side = 2;
            layer = 100;
            n = 2;
            
            c.rotate(side, layer, n, clockWise);
        } catch (Exception e) {
            assertEquals(LAYER_OUT_OF_RANGE_EXCEPTION_MSG, e.getMessage());
        }

        try {
            side = 1;
            layer = 0;
            n = -10;
            
            c.rotate(side, layer, n, clockWise);
        } catch (Exception e) {
            assertEquals(ROTATIONS_NEGATIVE_EXCEPTION_MSG, e.getMessage());
        }
    }

    @Test
    public void testRotateWithRotationSymmetries() throws Exception {
        int[][] symmetries = {
            {2, 2},
            {1, 3}
        };

        Cube c2 = new Cube();
        int side = 3;
        int layer = 0;
        boolean clockWise = true;

        for (int[] symmetry : symmetries) {
            c.rotate(side, layer, symmetry[0], clockWise);
            c2.rotate(side, layer, symmetry[1], !clockWise);

            assertArrayEquals(c.getCells(), c2.getCells());

            c = new Cube();
            c2 = new Cube();
        }
    }
}
