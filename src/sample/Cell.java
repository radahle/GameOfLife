package sample;

/**
 * Created by RudiAndre on 22.03.2016.
 */
public class Cell {
    private boolean state = false;
    private boolean newState;

    public Cell() {

    }

    public Cell(boolean state) {
        this.state = state;
    }

    public void setNewState(boolean state) {
        newState = state;
    }

    public void updateState() {
        state = newState;
    }

    public boolean getState() {
        return state;
    }
}
