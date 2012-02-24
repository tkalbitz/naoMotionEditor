/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.lifecycle;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;


public class AppMementoImpl implements IAppMemento {

    private List<NaoFrame> frameList;
    private NaoFrame selectedFrame;

    AppMementoImpl() {
    }

    void setFrames(List<NaoFrame> list) {
        final LinkedList<NaoFrame> listCopy = new LinkedList<NaoFrame>(list);
        frameList = listCopy;
    }

    public List<NaoFrame> getFrames() {
        return Collections.unmodifiableList(frameList);
    }

    public void setSelectedFrame(NaoFrame currentSelectedFrame) {
        selectedFrame = currentSelectedFrame;
    }

    public NaoFrame getSelectedFrame() {
        return selectedFrame;
    }
}
