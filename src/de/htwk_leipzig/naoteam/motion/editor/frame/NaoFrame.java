/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.frame;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.jdom.DataConversionException;
import org.jdom.Element;

import de.htwk_leipzig.naoteam.motion.editor.NaoConstants;
import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.TransformationFactory;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations.ScriptingTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.gui.panel.BodyPart;

/**
 * Contains all data for one frame and is immutable
 * 
 * @author Tobias Kalbitz
 */
public class NaoFrame {

	public static final NaoFrame EMPTY_FRAME = new NaoFrame(NaoConstants.STAND_ANGLES);

	protected NaoFrame() {
		selectedParts = new LinkedList<BodyPart>();
		actuators = new float[22];
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(actuators);
		result = prime * result
				+ ((selectedParts == null) ? 0 : selectedParts.hashCode());
		result = prime * result + (useOwnTransformation ? 1231 : 1237);
		return result;
	}

	@Override
	public String toString() {
		return "NaoFrame [actuators=" + Arrays.toString(actuators)
				+ ", selectedParts=" + selectedParts + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		NaoFrame other = (NaoFrame) obj;
		if (!Arrays.equals(actuators, other.actuators)) {
			return false;
		}
		if (selectedParts == null) {
			if (other.selectedParts != null) {
				return false;
			}
		} else if (!selectedParts.equals(other.selectedParts)) {
			return false;
		}
		if (useOwnTransformation != other.useOwnTransformation) {
			return false;
		}
		return true;
	}

	/**
	 * All Actuators as double array
	 */
	private final float[] actuators;

	/**
	 * selected body parts on creation of this frame
	 */
	private final List<BodyPart> selectedParts;

	/**
	 * Strategy which is used to transform from one frame to another
	 */
	private IFrameTransformationStrategy transformationStrategy;

	private boolean useOwnTransformation;
	
	public IFrameTransformationStrategy getTransformationStrategy() {
		return transformationStrategy;
	}

	/**
	 * @param transformationStrategy
	 *            the strategy to set
	 * @return a new {@link NaoFrame} instance, because it's immutable
	 */
	public NaoFrame setTransformationStrategy(
			IFrameTransformationStrategy transformationStrategy) {
		NaoFrame frame = new NaoFrame(actuators, selectedParts);
		frame.useOwnTransformation = useOwnTransformation;
		frame.transformationStrategy = transformationStrategy;

		return frame;
	}

	/**
	 * Constructs a new frame from a given array of doubles. The given array
	 * will be copied.
	 * 
	 * @param fs
	 *            array from which the frame is constructed
	 */
	public NaoFrame(final float[] fs) {
		this(fs, new LinkedList<BodyPart>());
	}

	/**
	 * Constructs a new frame from a given array of doubles. The given array
	 * will be copied.
	 * 
	 * @param fs
	 *            array from which the frame is constructed
	 * @param selectedParts
	 *            part which were selected on creation of this frame
	 */
	public NaoFrame(final float[] fs, final List<BodyPart> selectedParts) {
		if (fs.length != Actuator.values().length) {
			throw new IllegalArgumentException(String.format(
					"Array MUST HAVE %d elements (contains %d)!", Actuator
							.values().length, fs.length));
		}

		this.actuators = Arrays.copyOf(fs, fs.length);
		this.selectedParts = Collections
				.unmodifiableList(new LinkedList<BodyPart>(selectedParts));
		this.transformationStrategy = new ScriptingTransformationStrategy();
		this.useOwnTransformation = false;
	}

	/**
	 * Copy constructor
	 * @param frame
	 */
	public NaoFrame(NaoFrame frame) {
		this(frame.actuators, frame.selectedParts);
		useOwnTransformation = frame.useOwnTransformation;
		if(frame.transformationStrategy instanceof ScriptingTransformationStrategy) {
			this.transformationStrategy = new ScriptingTransformationStrategy((ScriptingTransformationStrategy) frame.transformationStrategy);
		} else {
			this.transformationStrategy = new ScriptingTransformationStrategy();
		}
	}

	public List<BodyPart> getSelectedParts() {
		return selectedParts;
	}

	/**
	 * @param ac
	 *            The actuator which position sould be returned
	 * @return the position of the actuator given in ac
	 */
	public double getActuatorPos(final Actuator ac) {
		return actuators[ac.getPosition()];
	}

	/**
	 * @return all actuator pos as array. The array is a copy.
	 */
	public float[] getAllActuatorPos() {
		return Arrays.copyOf(actuators, actuators.length);
	}

	/**
	 * Set a actuator for a given frame
	 * 
	 * @param ac
	 *            actuator to set
	 * @param value
	 *            value to set
	 * @return a new frame with the requested change
	 */
	public NaoFrame setActuator(Actuator ac, float value) {
		final float[] acPos = getAllActuatorPos();
		acPos[ac.getPosition()] = value;

		return new NaoFrame(acPos, selectedParts);
	}

	public void toXML(Element frameListElem) {
		Element e = new Element("frame");
		frameListElem.addContent(e);
		e.setAttribute("useOwnTransformation", Boolean.toString(useOwnTransformation));
		
		Element ac = new Element("actuators");
		e.addContent(ac);

		for (int i = 0; i < actuators.length; i++) {
			ac.addContent(new Element("ac").setAttribute("number",
					Integer.toString(i)).setAttribute("value",
					Float.toString(actuators[i])));
		}

		Element bodyParts = new Element("body-parts");
		e.addContent(bodyParts);
		
		for (BodyPart part : selectedParts) {
			bodyParts.addContent(new Element("part").setAttribute("name", part.name()));
		}

		transformationStrategy.toXML(e);
	}

	@SuppressWarnings("unchecked")
	public static NaoFrame loadXML(Element f) {
		List<Element> acs = f.getChild("actuators").getChildren();
		float [] angles = new float[NaoConstants.DOF];
		for (Element ac : acs) {
			angles[Integer.parseInt(ac.getAttributeValue("number"))] = Float.parseFloat(ac.getAttributeValue("value"));
		}
		
		List<Element> parts = f.getChild("body-parts").getChildren();
		List<BodyPart> partList = new LinkedList<BodyPart>();
		for (Element p : parts) {
			partList.add(BodyPart.valueOf(p.getAttributeValue("name")));
		}
		
		IFrameTransformationStrategy strg = TransformationFactory.loadXML(f.getChild("transformation"));
		
		NaoFrame frame = new NaoFrame(angles, partList);
		frame = frame.setTransformationStrategy(strg);
		
		try {
			frame.useOwnTransformation = f.getAttribute("useOwnTransformation").getBooleanValue();
		} catch (DataConversionException e) {
			e.printStackTrace();
		}

		
		return frame;
	}

	public void useOwnTransformation(boolean useOwnTransformation) {
		this.useOwnTransformation = useOwnTransformation;
	}

	public boolean useOwnTransformation() {
		return useOwnTransformation;
	}

	public void moveSlowlyTo(INaoCommunication comm) throws Exception {
		/* slowly move to position */
		float[] readedPos = comm.getBodyAngles();
		NaoFrame oldFrame = new NaoFrame(readedPos);
		List<NaoFrame> toFrames = oldFrame.getTransformationStrategy().transform(oldFrame, this);
		for (NaoFrame f : toFrames) {
			comm.playFrame(f);
		}
		System.out.println(toFrames.size());
	}
}
