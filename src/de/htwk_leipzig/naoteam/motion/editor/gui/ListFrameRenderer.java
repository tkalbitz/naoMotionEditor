/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;

@SuppressWarnings("serial")
public class ListFrameRenderer extends JLabel implements ListCellRenderer<NaoFrame>,
        Serializable {

    private final IImageLoader loader;
    
    /**
     * Constructs a default renderer object for an item in a list.
     */
    public ListFrameRenderer() {
        super();
        setOpaque(true);
        setBorder(BorderFactory.createLineBorder(getBackground()));
        setName("List.NaoFrame.cellRenderer");
        loader = MainApplication.injector.getInstance(IImageLoader.class);
    }

	public Component getListCellRendererComponent(
			JList<? extends NaoFrame> list, NaoFrame value, int index,
			boolean isSelected, boolean cellHasFocus) {
        setComponentOrientation(list.getComponentOrientation());

        Color bg = null;
        Color fg = null;

        JList.DropLocation dropLocation = list.getDropLocation();
        if (dropLocation != null && !dropLocation.isInsert()
                && dropLocation.getIndex() == index) {

            bg = list.getSelectionBackground().brighter().brighter();
            fg = list.getSelectionForeground().brighter().brighter();

            isSelected = true;
        }

        if (isSelected) {
            setBackground(bg == null ? list.getSelectionBackground() : bg);
            setForeground(fg == null ? list.getSelectionForeground() : fg);
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        
        if(!(value instanceof NaoFrame)) {
            throw new IllegalArgumentException("Renderer Works only for Naoframes");
        }

        NaoFrame frame = value;
        setIcon(loader.small("image-x-generic.png"));
        setText(Integer.toHexString(frame.hashCode()));

        setEnabled(list.isEnabled());
        setFont(list.getFont());

        Border border = null;
        if (cellHasFocus) {
            if (isSelected) {
                border = BorderFactory.createLineBorder(getBackground()
                        .darker().darker());
            }
            if (border == null) {
                border = BorderFactory.createLineBorder(getBackground()
                        .darker());
            }
        } else {
            border = BorderFactory.createLineBorder(list.getBackground());
        }
        setBorder(border);

        return this;
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     * 
     * @since 1.5
     * @return <code>true</code> if the background is completely opaque and
     *         differs from the JList's background; <code>false</code> otherwise
     */
    @Override
    public boolean isOpaque() {
        Color back = getBackground();
        Component p = getParent();
        if (p != null) {
            p = p.getParent();
        }
        // p should now be the JList.
        boolean colorMatch = (back != null) && (p != null)
                && back.equals(p.getBackground()) && p.isOpaque();
        return !colorMatch && super.isOpaque();
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void validate() {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     * 
     * @since 1.5
     */
    @Override
    public void invalidate() {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     * 
     * @since 1.5
     */
    @Override
    public void repaint() {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void revalidate() {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void repaint(Rectangle r) {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue,
            Object newValue) {
        // Strings get interned...
        if (propertyName.equals("text")
                || ((propertyName.equals("font") 
                || propertyName.equals("foreground"))
                && oldValue != newValue && getClientProperty(javax.swing.plaf.basic.BasicHTML.propertyKey) != null)) {

            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, byte oldValue,
            byte newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, char oldValue,
            char newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, short oldValue,
            short newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, int oldValue,
            int newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, long oldValue,
            long newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, float oldValue,
            float newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, double oldValue,
            double newValue) {
    }

    /**
     * Overridden for performance reasons. See the <a
     * href="#override">Implementation Note</a> for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue,
            boolean newValue) {
    }
}
