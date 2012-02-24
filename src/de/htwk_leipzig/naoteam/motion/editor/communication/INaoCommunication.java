/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.communication;

import java.io.IOException;

import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;

/**
 * Communication with the nao.
 *
 * @author Tobias Kalbitz
 */
public interface INaoCommunication {
	public void connect(String host, int port) throws Exception;

	public float[] getBodyAngles() throws IOException;

	public void exitMotionPlayer() throws Exception;

	void setJointPosition(int joint, float angle, int time) throws Exception;

	void setJointStiffness(int joint, float stiffness, int time) throws Exception;
	
	void startRec(int i) throws Exception;
	void stopRec() throws Exception;

	void playFrame(NaoFrame frame) throws Exception;
	
	boolean isConnected();
}
