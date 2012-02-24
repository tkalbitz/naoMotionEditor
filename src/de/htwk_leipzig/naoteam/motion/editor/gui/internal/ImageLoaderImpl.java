/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui.internal;

import javax.swing.ImageIcon;

import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;

public class ImageLoaderImpl implements IImageLoader {
    private final static String SMALL_ICON_PREFIX = "/img/16x16/";
    private final static String LARGE_ICON_PREFIX = "/img/22x22/";
    
    public ImageIcon small(String name) {
        return new ImageIcon(getClass().getResource(SMALL_ICON_PREFIX + name));
    }

    public ImageIcon large(String name) {
        return new ImageIcon(getClass().getResource(LARGE_ICON_PREFIX + name));
    }

}
