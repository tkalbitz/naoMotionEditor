/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces;

/**
 * Set a constraint on a config value.
 * @author Tobias Kalbitz
 *
 * @param <T> type of the constraint
 */
public interface INumberConstraint<T extends Number> {
	
	/**
	 * A minimal constraint for a given value
	 * @param value the min value
	 * @return the {@link INumberConstraint}
	 */
	INumberConstraint<T> min(T value);
	
	/**
	 * A maxiaml constraint for a given value
	 * @param value the max value
	 * @return the {@link INumberConstraint}
	 */
	INumberConstraint<T> max(T value);
}
