/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pcbdraw.gui.progress;

import java.util.ArrayList;

/**
 *
 * @author Nick Berryman
 */
public abstract class Progressible {
    private ArrayList<ProgressListener> progressListeners = new ArrayList<>();
    private int maxProgress = 100;
    private int progress = 0;
    public void addProgressListener(ProgressListener p){progressListeners.add(p);}
    protected void setMaxProgress(int newMax){maxProgress = newMax; progressAll();}
    protected void setProgress(int progress){this.progress = progress; progressAll();}
    protected void incrementProgress(){this.progress++; this.progressAll();}
    private void progressAll(){for (ProgressListener p : progressListeners) p.progress(progress/(double)maxProgress);}
}
