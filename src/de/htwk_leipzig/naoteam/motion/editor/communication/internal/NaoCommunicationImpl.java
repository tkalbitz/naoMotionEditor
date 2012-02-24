/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.communication.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import de.htwk_leipzig.naoteam.motion.editor.NaoConstants;
import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;

public class NaoCommunicationImpl implements INaoCommunication {

    private boolean connected;

	protected NaoCommunicationImpl() {

    }

	private static final int CMD_GET_ANGLES    =   1;
	private static final int CMD_SET_POS       =   2;
	private static final int CMD_SET_STIFF     =   3;
	private static final int CMD_SET_FRAME     =   4;
	private static final int CMD_START_REC	   =   5;
	private static final int CMD_STOP_REC	   =   6;
	private static final int CMD_EXIT          = 255;
	
	private static final int DOF = 22;
	private static DataOutputStream ostream;
	private static DataInputStream istream;
	
	public void setJointPosition(int joint, float angle, int time) throws Exception {
		ostream.writeInt(CMD_SET_POS);
		ostream.writeInt(joint);
		ostream.writeFloat(angle);
		ostream.writeInt(time);		
	}

	public void setJointStiffness(int joint, float stiffness, int time) throws Exception {
		ostream.writeInt(CMD_SET_STIFF);
		ostream.writeInt(joint);
		ostream.writeFloat(stiffness);
		ostream.writeInt(time);		
	}
	
	public void startRec(int frames) throws Exception {
		ostream.writeInt(CMD_START_REC);	
		ostream.writeInt(frames);	
	}
	public void stopRec() throws Exception {
		ostream.writeInt(CMD_STOP_REC);	
	}
	
	public void playFrame(NaoFrame frame) throws Exception {
		
		System.out.println(frame);

		ostream.writeInt(CMD_SET_FRAME);
		ostream.writeFloat(NaoConstants.STIFF_MOVE);
		ostream.writeInt(NaoConstants.STIFF_TICKS);
		ostream.writeInt(NaoConstants.POS_TICKS);
		
		float [] angles = frame.getAllActuatorPos();
		for (float a : angles) {
			ostream.writeFloat(a);
		}
	}
	
	public void connect(String host, int port) throws Exception {
		Socket s = new Socket(host, port);
		istream = new DataInputStream(s.getInputStream());
		ostream = new DataOutputStream(s.getOutputStream());
		connected = true;
	}

	public float[] getBodyAngles() throws IOException {
		ostream.writeInt(CMD_GET_ANGLES);
		
		float [] angles = new float[DOF];
		
		for(int i = 0; i < DOF; i++) {
			angles[i] = istream.readFloat();
		}
		
		return angles;
	}
	
	public void exitMotionPlayer() throws Exception {
		ostream.writeInt(CMD_EXIT);
	}

	public boolean isConnected() {
		return connected;
	}
}
