/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame;

/**
 * Contains all actuators from the Nao. Each one has a number which represents the number in the string
 * @author Tobias Kalbitz
 */
public enum Actuator {
	HeadYaw(0),
	HeadPitch(1),
	LShoulderPitch(2),
	LShoulderRoll(3),
	LElbowYaw(4),
	LElbowRoll(5),
	LHipYawPitch(6),
	LHipRoll(7),
	LHipPitch(8),
	LKneePitch(9),
	LAnklePitch(10),
	LAnkleRoll(11),
	RHipYawPitch(12),
	RHipRoll(13),
	RHipPitch(14),
	RKneePitch(15),
	RAnklePitch(16),
	RAnkleRoll(17),
	RShoulderPitch(18),
	RShoulderRoll(19),
	RElbowYaw(20),
	RElbowRoll(21);
	
	public final int position;
	public final String paramName;
	
	private Actuator(int pos) {
		position = pos;
		String tmp = toString();
		
		if(tmp.contains("YawPitch"))
		    paramName = "YPitch";
		else
		    paramName = toString().replaceAll(".*(YawPitch|Pitch|Roll|Yaw).*", "$1");
	}

	public int getPosition() {
		return position;
	}
}
