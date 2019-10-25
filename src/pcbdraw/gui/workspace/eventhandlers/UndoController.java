/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.workspace.eventhandlers;

import java.util.Stack;

/**
 *
 * @author Nick Berryman
 */
public class UndoController {
    private final Stack<ReversibleAction> undoStack = new Stack<>();
    private final Stack<ReversibleAction> redoStack = new Stack<>();
    
    public void add(ReversibleAction action){
        redoStack.clear();
        undoStack.push(action);
    }
    
    public void undo(){
        if (undoStack.size() > 0){
            ReversibleAction action = undoStack.pop();
            action.undo();
            redoStack.push(action);
        }
    }
    
    public void redo(){
        if (redoStack.size() > 0){
            ReversibleAction action = redoStack.pop();
            action.redo();
            undoStack.push(action);
        }
    }
    
    public void clear(){
        undoStack.clear();
        redoStack.clear();
    }
}
