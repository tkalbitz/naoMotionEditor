/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.communication.internal;

import java.io.IOException;

import de.htwk_leipzig.naoteam.motion.editor.NaoConstants;
import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;

public class DummyNaoCommunicationImpl implements INaoCommunication {

	public void connect(String host, int port) throws Exception {
	}

	public void exitMotionPlayer() throws Exception {
	}

	public float[] getBodyAngles() throws IOException {
		return new float[NaoConstants.DOF];
	}

	public boolean isConnected() {
		return true;
	}

	public void playFrame(NaoFrame frame) throws Exception {
	}

	public void setJointPosition(int joint, float angle, int time)
			throws Exception {
	}

	public void setJointStiffness(int joint, float stiffness, int time)
			throws Exception {
	}

	public void startRec(int frames) throws Exception {
	}

	public void stopRec() throws Exception {
	}

}
