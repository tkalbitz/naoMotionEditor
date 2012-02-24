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
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

/**
 * Set the robot in the current frame.
 *
 * @author Tobias Kalbitz
 */
@SuppressWarnings("serial")
public class ActionFollowFrame extends AbstractAction  {

    /** controller to manipulate frames */
    protected MainApplication app;

    @Inject
    ActionFollowFrame(IImageLoader loader, MainApplication appController) {
        super("Follow frame on robot");
        final String name = "system-shutdown.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        putValue(Action.SELECTED_KEY, Boolean.FALSE);
        app = appController;
        putValue(SHORT_DESCRIPTION, "Follow the current selected frame on the robot.");
    }

    public void actionPerformed(ActionEvent e) {
    }
}
