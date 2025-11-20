package com.comp2042.GameBoard;

/** downward move operation
 * <p>Holds the ViewData describing the current falling brick after the move
 * and an optional ClearRow describing any rows cleared as a result of the move.</p>*/
public final class DownData {

    /** Cleared rows */
    private final ClearRow clearRow;

    /** View Data */
    private final ViewData viewData;

    /** Create a DownData result.
     * @param clearRow information about cleared rows
     * @param viewData the current falling brick view data after the move*/
    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    /** gets new cleared rows
     * @return the ClearRow describing removed lines and resulting matrix, or null if no rows were cleared*/
    public ClearRow getClearRow() {
        return clearRow;
    }

    /** get new view data
     * @return the ViewData snapshot for the moving brick after the downward move*/
    public ViewData getViewData() {
        return viewData;
    }
}