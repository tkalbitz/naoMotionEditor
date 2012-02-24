/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor;

/**
 * @author Tobias Kalbitz
 */
public class NaoConstants {
	private NaoConstants() {
		/* only constants */
	}

	public static final float STIFF_MAX = 1.f;
	public static final float STIFF_MIN = 0.f;
	public static final float STIFF_MOVE = 1.f;
	public static final int STIFF_TICKS = 0;
	public static final int POS_TICKS = 0;

	public static final int DOF = 22;

	public static final float[] STAND_ANGLES = { 0.f, 0.f,
		1.868f, 0.284f, -1.6f, -0.584f,
		0.f, 0.0f, 0.00f, 0.2f, -0.2f, 0.f,
		0.f, 0.0f, 0.00f, 0.2f, -0.2f, 0.f,
		1.868f, -0.284f, 1.6f, 0.584f };
}
