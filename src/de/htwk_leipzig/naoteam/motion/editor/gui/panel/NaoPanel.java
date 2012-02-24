/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui.panel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.LayoutMap;

import de.htwk_leipzig.naoteam.motion.editor.NaoConstants;
import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.Actuator;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations.LinearTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;
import de.htwk_leipzig.naoteam.motion.editor.gui.border.NaoBorderFactory;

public class NaoPanel {

    final private HashMap<Actuator, JTextField> textFields;
    final private HashMap<BodyPart, JToggleButton> buttons;

    private NaoFrame frame;

    private final JPanel contentPanel;

	private JToggleButton btnSetStiffness;

    public NaoPanel() {
        textFields = new HashMap<Actuator, JTextField>();
        buttons = new HashMap<BodyPart, JToggleButton>();
        initComponents();
        contentPanel = buildPanel();
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }
    
    public boolean isStiff() {
    	return btnSetStiffness.isSelected();
    }

    /**
     * Initialize all visual components shown in the panel
     */
    private void initComponents() {
        final int COLUMN_COUNT = 10;

        for (final Actuator ac : Actuator.values()) {
            textFields.put(ac, new JTextField(COLUMN_COUNT));
        }

        for (final BodyPart part : BodyPart.values()) {
            buttons.put(part, new JToggleButton(part.action));
        }
    }

    /**
     * Layout the components from the panel
     */
    private JPanel buildPanel() {
    	
    	btnSetStiffness = new JToggleButton("Set Stiff");
    	btnSetStiffness.setMnemonic(KeyEvent.VK_SPACE);
    	btnSetStiffness.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				INaoCommunication comm = MainApplication.comm;

				try {

					if (btnSetStiffness.isSelected()) {
						NaoFrame frame = null;
						frame = new NaoFrame(comm.getBodyAngles());
						LinearTransformationStrategy strg = new LinearTransformationStrategy(5);
						frame = frame.setTransformationStrategy(strg);
						frame.moveSlowlyTo(comm);

						for (Actuator ac : Actuator.values()) {
							comm.setJointStiffness(ac.position, 0.1f, 0);
						}
						Thread.sleep(100);
						for (Actuator ac : Actuator.values()) {
							comm.setJointStiffness(ac.position, NaoConstants.STIFF_MAX, 0);
						}
					} else {
						/* only set selected things */
						for (BodyPart bodyPart : BodyPart.values()) {

							/* if not selected to nothing */
							Object o = bodyPart.action.getValue(Action.SELECTED_KEY);
							if(!(o instanceof Boolean) || (Boolean)o == Boolean.FALSE) {
								continue;
							}
							
							for (Actuator a : bodyPart.dependendActuators) {
								MainApplication.comm.setJointStiffness(a.position, NaoConstants.STIFF_MIN, 0);
							}

							/* set dependent parts */
							for (BodyPart part : bodyPart.dependendParts) {
								for (Actuator a : part.dependendActuators) {
									MainApplication.comm.setJointStiffness(a.position, NaoConstants.STIFF_MIN, 0);
								}
							}
						}
					}

				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		});
    	
        LayoutMap.getRoot().columnPut("btn", "fill:[pref,50dlu]");
        LayoutMap.getRoot().columnPut("txt", "fill:[pref,44dlu]");
        LayoutMap.getRoot().columnPut("space", "5dlu");

        final FormLayout mainLayout = new FormLayout(
                "$btn, $lcgap, right:pref, $lcgap, $txt, $space, " +
                "$btn, $lcgap, right:pref, $lcgap, $txt, ",
                "fill:pref, $lgap," + /* part button */
                "fill:pref, $lgap, fill:pref, 15dlu, " +

                "fill:pref, $lgap," + /* part button */
                "fill:pref, $lgap, fill:pref, 10dlu, " +
                "fill:pref, $lgap, fill:pref, 15dlu, " +

                "fill:pref, $lgap," + /* part button */
                "fill:pref, $lgap, fill:pref, $lgap, fill:pref, 10dlu, " +
                "fill:pref, 10dlu, " +
                "fill:pref, $lgap, fill:pref"
                );

        final int LCOL_BTN = 1;
        final int LCOL_LBL = 3;
        final int LCOL_TXT = 5;

        final int RCOL_BTN = 7;
        final int RCOL_LBL = 9;
        final int RCOL_TXT = 11;

        mainLayout.setColumnGroups(new int[][] {
                {LCOL_BTN, RCOL_BTN},
                {LCOL_LBL, RCOL_LBL},
                {LCOL_TXT, RCOL_TXT},
        });

        final PanelBuilder builder = new PanelBuilder(mainLayout);
        builder.setBorder(NaoBorderFactory.getInnerFrameBorder());
        int row = 1;
        
        /* Head */
        builder.add(buttons.get(BodyPart.Body), CC.xywh(LCOL_BTN, row, 11, 1));
        row++; /* empty row */
        row++;
        builder.add(buttons.get(BodyPart.Head), CC.xywh(LCOL_BTN, row, 1, 3));
        builder.addLabel(Actuator.HeadPitch.paramName, CC.xy(LCOL_LBL, row));
        builder.add(textFields.get(Actuator.HeadPitch), CC.xy(LCOL_TXT, row));
        builder.add(btnSetStiffness, CC.xywh(RCOL_BTN, row, 5, 3));
        row++; /*gap */
        row++;
        builder.addLabel(Actuator.HeadYaw.paramName, CC.xy(LCOL_LBL, row));
        builder.add(textFields.get(Actuator.HeadYaw), CC.xy(LCOL_TXT, row));

        /* shoulder */
        row++; /*gap*/
        row++;
        builder.add(buttons.get(BodyPart.LArm), CC.xywh(LCOL_BTN, row, 5, 1));
        builder.add(buttons.get(BodyPart.RArm), CC.xywh(RCOL_BTN, row, 5, 1));
        row++; /*gap*/
        row++;
        builder.add(buttons.get(BodyPart.LShoulder), CC.xywh(LCOL_BTN, row, 1, 3));
        builder.add(buttons.get(BodyPart.RShoulder), CC.xywh(RCOL_BTN, row, 1, 3));
        builder.addLabel(Actuator.LShoulderPitch.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RShoulderPitch.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LShoulderPitch), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RShoulderPitch), CC.xy(RCOL_TXT, row));
        row++; /*gap */
        row++;
        builder.addLabel(Actuator.LShoulderRoll.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RShoulderRoll.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LShoulderRoll), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RShoulderRoll), CC.xy(RCOL_TXT, row));

        /* Elbow */
        row++; /* empty row */
        row++;
        builder.add(buttons.get(BodyPart.LElbow), CC.xywh(LCOL_BTN, row, 1, 3));
        builder.add(buttons.get(BodyPart.RElbow), CC.xywh(RCOL_BTN, row, 1, 3));
        builder.addLabel(Actuator.LElbowYaw.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RElbowYaw.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LElbowYaw), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RElbowYaw), CC.xy(RCOL_TXT, row));
        row++; /*gap */
        row++;
        builder.addLabel(Actuator.LElbowRoll.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RElbowRoll.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LElbowRoll), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RElbowRoll), CC.xy(RCOL_TXT, row));

        /* hip */
        row++; /* empty row */
        row++;
        builder.add(buttons.get(BodyPart.LLeg), CC.xywh(LCOL_BTN, row, 5, 1));
        builder.add(buttons.get(BodyPart.RLeg), CC.xywh(RCOL_BTN, row, 5, 1));
        row++; /*gap*/
        row++;
        builder.add(buttons.get(BodyPart.LHip), CC.xywh(LCOL_BTN, row, 1, 5));
        builder.add(buttons.get(BodyPart.RHip), CC.xywh(RCOL_BTN, row, 1, 5));
        builder.addLabel(Actuator.LHipPitch.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RHipPitch.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LHipPitch), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RHipPitch), CC.xy(RCOL_TXT, row));
        row++; /*gap*/
        row++;
        builder.addLabel(Actuator.LHipRoll.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RHipRoll.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LHipRoll), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RHipRoll), CC.xy(RCOL_TXT, row));
        row++; /*gap*/
        row++;
        builder.addLabel(Actuator.LHipYawPitch.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RHipYawPitch.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LHipYawPitch), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RHipYawPitch), CC.xy(RCOL_TXT, row));

        /* knee */
        row++; /* empty row */
        row++;
        builder.add(buttons.get(BodyPart.LKnee), CC.xywh(LCOL_BTN, row, 1, 1));
        builder.add(buttons.get(BodyPart.RKnee), CC.xywh(RCOL_BTN, row, 1, 1));
        builder.addLabel(Actuator.LKneePitch.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RKneePitch.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LKneePitch), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RKneePitch), CC.xy(RCOL_TXT, row));

        /* Ankle */
        row++; /* empty row */
        row++;
        builder.add(buttons.get(BodyPart.LAnkle), CC.xywh(LCOL_BTN, row, 1, 3));
        builder.add(buttons.get(BodyPart.RAnkle), CC.xywh(RCOL_BTN, row, 1, 3));
        builder.addLabel(Actuator.LAnklePitch.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RAnklePitch.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LAnklePitch), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RAnklePitch), CC.xy(RCOL_TXT, row));
        row++; /*gap */
        row++;
        builder.addLabel(Actuator.LAnkleRoll.paramName, CC.xy(LCOL_LBL, row));
        builder.addLabel(Actuator.RAnkleRoll.paramName, CC.xy(RCOL_LBL, row));
        builder.add(textFields.get(Actuator.LAnkleRoll), CC.xy(LCOL_TXT, row));
        builder.add(textFields.get(Actuator.RAnkleRoll), CC.xy(RCOL_TXT, row));

        return builder.getPanel();
    }

    public void setFrame(NaoFrame frame) {
        this.frame = frame;

        final float[] pos = frame.getAllActuatorPos();

        for (final Actuator ac : Actuator.values()) {
            textFields.get(ac).setText(String.format("%+09.6f",pos[ac.position]));
        }
    }

    public NaoFrame getFrame() {
        return frame;
    }

    /**
     * @return the body parts that are currently activated.
     */
    public List<BodyPart> getSelectedBodyParts() {
        final List<BodyPart> parts = new LinkedList<BodyPart>();

        for (final BodyPart bodyPart : BodyPart.values()) {
            if(buttons.get(bodyPart).isSelected()) {
                parts.add(bodyPart);
            }
        }

        return parts;
    }
}
