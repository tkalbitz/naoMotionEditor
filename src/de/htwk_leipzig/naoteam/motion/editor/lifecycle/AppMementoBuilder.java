/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.lifecycle;

import java.util.List;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;

/**
 * Build a {@link IAppMemento} object. There should be a test of a valid state.
 * @author Tobi
 *
 */
public class AppMementoBuilder {

    private final AppMementoImpl memento;

    public AppMementoBuilder() {
        memento = new AppMementoImpl();
    }

    public void setFrames(List<NaoFrame> list) {
        memento.setFrames(list);
    }

    public IAppMemento createMemento() {
        return memento;
    }

    public void setSelectedFrame(NaoFrame currentSelectedFrame) {
        memento.setSelectedFrame(currentSelectedFrame);
    }
}
