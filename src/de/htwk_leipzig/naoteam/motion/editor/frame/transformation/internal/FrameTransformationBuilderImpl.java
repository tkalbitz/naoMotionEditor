/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame.transformation.internal;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jgoodies.forms.builder.DefaultFormBuilder;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.FormLayout;

import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationConfigBuilder;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.INumberConstraint;

public class FrameTransformationBuilderImpl implements
		IFrameTransformationConfigBuilder {

	protected DefaultFormBuilder builder;

	protected Object bean;

	private final FormLayout layout;

	/**
	 * We work with dependency injection. This class must not created explicit.
	 */
	public FrameTransformationBuilderImpl() {
		layout = new FormLayout("right:pref, $lcgap, fill:10dlu:grow");
		builder = new DefaultFormBuilder(layout /*, new FormDebugPanel() */);
	}


	protected void addRelatedRow(String caption, JComponent c) {
		builder.append(caption, c);
	}

	protected void addUnrelatedRow(String name) {
	    builder.appendRow(FormFactory.PARAGRAPH_GAP_ROWSPEC);
	    builder.nextLine();
	    builder.appendRow(FormFactory.DEFAULT_ROWSPEC);
	    builder.nextLine();
	    builder.append(new JLabel(name), 3);
	    builder.nextLine();
	}

	/**
	 * {@inheritDoc}
	 */
	public INumberConstraint<Double> addDouble(JComponent field, String name, String caption) {
		/* TODO: !!! */
		addRelatedRow(caption, field);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public INumberConstraint<Float> addFloat(JComponent field, String name, String caption) {
		addRelatedRow(caption, field);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public INumberConstraint<Integer> addInteger(JComponent field, String name, String caption) {
		addRelatedRow(caption, field);
		return null;
	}

	public void addString(JComponent field, String name, String caption) {
		addRelatedRow(caption, field);
	}

	/**
	 * {@inheritDoc}
	 */
	public IFrameTransformationConfigBuilder addSeparator(String caption) {
		addUnrelatedRow(caption);
		return this;
	}

	public JPanel createPanel() {
		return builder.getPanel();
	}
}
