/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui.border;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.border.AbstractBorder;

/**
 * Border used to summarize left parts of the body 
 * @author Tobias Kalbitz
 *
 */
final class SumRightBorder extends AbstractBorder {

    private static final long serialVersionUID = 2789762081582053429L;

    private static final Insets INSETS = new Insets(2, 2, 1, 1);

    private final Color border = Color.BLACK;

    public Insets getBorderInsets(Component c) { return INSETS; }

    public void paintBorder(Component c, Graphics g,
        int x, int y, int w, int h) {

        g.translate(x, y);

        g.setColor(border);
        g.fillRect(1, 0, w - 1, 1);
        
        g.fillRect(w - 1, 1, 1, h - 2);
        
        g.fillRect(1, h - 1, w - 1, 1);
        g.translate(-x, -y);
    }

}
