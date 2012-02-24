/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
    package de.htwk_leipzig.naoteam.motion.editor.gui.action;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JSlider;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.google.inject.Inject;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.ButtonBarFactory;
import com.jgoodies.forms.layout.FormLayout;

import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations.EmptyTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;

/**
 * Set the robot in the current frame.
 *
 * @author Tobias Kalbitz
 */
@SuppressWarnings("serial")
public class ActionCreateCalcKeyFrame extends AbstractAction  {

	/** communication with the nao is done by this interface */
    private INaoCommunication comm;

    /** controller to manipulate frames */
    protected MainApplication app;

    @Inject
    ActionCreateCalcKeyFrame(IImageLoader loader, MainApplication appController) {
        super("Play anim on robot");
        final String name = "kcalc.png";
        putValue(Action.LARGE_ICON_KEY, loader.large(name));
        putValue(Action.SMALL_ICON, loader.small(name));
        app = appController;
        putValue(SHORT_DESCRIPTION, "Create a keyframe from a calculated frame");
    }

    public void actionPerformed(ActionEvent e) {

    	NaoFrame src = app.getCurrentSelectedFrame();
    	int pos = app.getCurrentSelectedFrameIndex();

    	/* not in list or last one */
    	if(pos == -1 || pos == app.getFrameList().size() - 1) {
    		return;
    	}

    	NaoFrame to = app.getFrameList().get(pos + 1);
    	
    	IFrameTransformationStrategy strg = src.getTransformationStrategy();
    	if(strg instanceof EmptyTransformationStrategy) {
    		strg = app.getStandardStrategy();
    	}

    	List<NaoFrame> frameList = strg.transform(src, to);
    	final NaoFrame[] frames = frameList.toArray(new NaoFrame[0]);
    	frameList.clear();
    	
    	createUI(frames, pos, pos + 1);
    }

	private void createUI(final NaoFrame[] frames, final int srcPos, final int toPos) {
    	final JDialog dialog = new JDialog(app.getMainWindow(), "Select Frame", true);
    	dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		final JSlider slider = new JSlider(0, frames.length - 1);
    	slider.setValue(0);
    	slider.addChangeListener(new ChangeListener() {
			
			public void stateChanged(ChangeEvent e) {
				try {
					comm.playFrame(frames[slider.getValue()]);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		});
    	
    	final JButton btnOk = new JButton("Ok");
    	btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				app.getFrameList().add(toPos, frames[slider.getValue()]);
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
    	
    	final JButton btnCancel = new JButton("Cancel");
    	btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				dialog.dispose();
			}
		});
    	
    	FormLayout layout = new FormLayout("left:pref, $lcgap, fill:640px:grow", "pref, $lgap, pref");
    	PanelBuilder builder = new PanelBuilder(layout /*, new FormDebugPanel() */);
    	builder.addLabel("Frame:");
    	builder.nextColumn(2);
    	builder.add(slider);
    	builder.nextRow(2);
    	builder.add(ButtonBarFactory.buildOKCancelBar(btnOk, btnCancel));
    	builder.setDefaultDialogBorder();
    	
    	dialog.add(builder.getPanel());
    	dialog.pack();
    	
        final Dimension paneSize = dialog.getSize();
        final Dimension screenSize = dialog.getToolkit().getScreenSize();
        dialog.setLocation((screenSize.width - paneSize.width) / 2,
                (int) ((screenSize.height - paneSize.height) * 0.45));
        
    	dialog.setVisible(true);
	}

	public void setComm(INaoCommunication comm) {
		this.comm = comm;
	}

	public INaoCommunication getComm() {
		return comm;
	}
}
