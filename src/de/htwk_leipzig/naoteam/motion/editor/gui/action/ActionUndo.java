/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

import com.google.inject.Inject;

import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.IUndoRedoListener;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

@SuppressWarnings("serial")
public class ActionUndo extends AbstractAction implements IUndoRedoListener {

    private final MainApplication app;

    @Inject
    public ActionUndo(MainApplication appl, IImageLoader loader) {
        final String name = "undo.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        app = appl;
        app.addUndoRedoListener(this);
    }

    public void actionPerformed(ActionEvent e) {
        app.undo();
    }

    public void undoRedoStateChanged(boolean undoIsAvailable, boolean redoIsAvailable) {
        setEnabled(undoIsAvailable);
    }

}
