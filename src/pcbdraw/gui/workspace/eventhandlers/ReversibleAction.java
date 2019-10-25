/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.eventhandlers;

/**
 *
 * @author Nick Berryman
 */
public abstract class ReversibleAction {
    public abstract void redo();
    public abstract void undo();
}
