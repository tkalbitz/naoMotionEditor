/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui.panel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;

import de.htwk_leipzig.naoteam.motion.editor.frame.Actuator;

public enum BodyPart {
    Head("Head", new BodyPart[] {}, new Actuator[] {Actuator.HeadPitch, Actuator.HeadYaw}),
    LShoulder("Left Shoulder", new BodyPart[] {}, new Actuator[] {Actuator.LShoulderPitch, Actuator.LShoulderRoll}),
    LElbow("Left Elbow", new BodyPart[] {}, new Actuator[] {Actuator.LElbowRoll, Actuator.LElbowYaw}),
    LHip("Left Hip", new BodyPart[] {}, new Actuator[] {Actuator.LHipPitch, Actuator.LHipRoll, Actuator.LHipYawPitch}),
    LKnee("Left Knee", new BodyPart[] {}, new Actuator[] {Actuator.LKneePitch}),
    LAnkle("Left Ankle", new BodyPart[] {}, new Actuator[] {Actuator.LAnklePitch, Actuator.LAnkleRoll}),
    RShoulder("Right Shoulder", new BodyPart[] {}, new Actuator[] {Actuator.RShoulderPitch, Actuator.RShoulderRoll}),
    RElbow("Right Elbow", new BodyPart[] {}, new Actuator[] {Actuator.RElbowRoll, Actuator.RElbowYaw}),
    RHip("Right Hip", new BodyPart[] {}, new Actuator[] {Actuator.RHipPitch, Actuator.RHipRoll, Actuator.RHipYawPitch}),
    RKnee("Right Knee", new BodyPart[] {}, new Actuator[] {Actuator.RKneePitch}),
    RAnkle("Right Ankle", new BodyPart[] {}, new Actuator[] {Actuator.RAnklePitch, Actuator.RAnkleRoll}),
    LArm("Left Arm", new BodyPart[] {LShoulder, LElbow}, new Actuator[] {}),
    RArm("Right Arm", new BodyPart[] {RShoulder, RElbow}, new Actuator[] {}),
    LLeg("Left Leg", new BodyPart[] {LHip, LKnee, LAnkle}, new Actuator[] {}),
    RLeg("Right Leg", new BodyPart[] {RHip, RKnee, RAnkle}, new Actuator[] {}),
    Body("Body", new BodyPart[] {Head, LShoulder, LElbow, LKnee, LHip, LAnkle, RShoulder, RElbow, RHip, RKnee, RAnkle}, new Actuator[] {});

    public final String name;
    public final List<BodyPart> dependendParts;
    public final List<Actuator> dependendActuators;
    public final Action action;

    BodyPart(String name, BodyPart[] depParts, Actuator[] depActuators) {
        this.name = name;
        dependendParts = Collections.unmodifiableList(new LinkedList<BodyPart>(Arrays.asList(depParts)));
        dependendActuators = Collections.unmodifiableList(new LinkedList<Actuator>(Arrays.asList(depActuators)));
        action = new BodyPartAction(this);

        for (final BodyPart bodyPart : depParts) {
            bodyPart.action.addPropertyChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    if(!evt.getPropertyName().equals(Action.SELECTED_KEY)) {
                        return;
                    }

                    if(!(evt.getNewValue() instanceof Boolean)) {
                        return;
                    }

                    if((Boolean)evt.getNewValue() == true) {
                        return;
                    }

                    if((Boolean)action.getValue(Action.SELECTED_KEY) == false) {
                        return;
                    }

                    action.putValue(Action.SELECTED_KEY, false);
                }
            });
        }
    }
}
