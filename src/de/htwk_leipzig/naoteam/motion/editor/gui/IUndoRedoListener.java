/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui;

/**
 * Is fired when the undo/redo state changes
 * 
 * @author Tobias Kalbitz
 */
public interface IUndoRedoListener {
    void undoRedoStateChanged(boolean undoIsAvailable, boolean redoIsAvailable);
}
