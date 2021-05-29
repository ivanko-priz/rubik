package rubik;

enum SideDirection {
    TOP,
    BOTTOM,
    LEFT,
    RIGHT;

    public SideDirection getOpposite() {
        switch(this) {
            case TOP:
                return BOTTOM;
            case BOTTOM:
                return TOP;
            case LEFT:
                return RIGHT;
            default:
                return LEFT;
        }
    }

    public SideDirection getAdjacent() {
        switch(this) {
            case TOP:
                return RIGHT;
            case BOTTOM:
                return LEFT;
            case LEFT:
                return BOTTOM;
            default:
                return TOP;
        }
    }

}
