/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXTitledPanel;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.jgoodies.binding.list.LinkedListModel;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

import de.htwk_leipzig.naoteam.motion.editor.NaoConstants;
import de.htwk_leipzig.naoteam.motion.editor.NaoModule;
import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.implementations.ScriptingTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationStrategy;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionCreateCalcKeyFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionDeleteFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionDuplicateFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionFollowFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionMirrorFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionMotionExport;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionMoveFrameDown;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionMoveFrameUp;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionOpen;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionPlayAnim;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionPlayCurrentFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionRecordPostion;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionSave;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionSaveAs;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionStand;
import de.htwk_leipzig.naoteam.motion.editor.gui.action.ActionUpdateFrame;
import de.htwk_leipzig.naoteam.motion.editor.gui.border.NaoBorderFactory;
import de.htwk_leipzig.naoteam.motion.editor.gui.panel.BodyPart;
import de.htwk_leipzig.naoteam.motion.editor.gui.panel.NaoPanel;
import de.htwk_leipzig.naoteam.motion.editor.gui.panel.PropertyPanel;
import de.htwk_leipzig.naoteam.motion.editor.lifecycle.AppMementoBuilder;
import de.htwk_leipzig.naoteam.motion.editor.lifecycle.IAppMemento;

public class MainApplication {

    private static final String PREF_IP_ADRESS = "ipAdress";
	private JList<NaoFrame> frameList;
    private JXTitledPanel frameListContainer;
    private JXTitledPanel naoOverviewContainer;
    private JXTitledPanel propContainer;
    private NaoPanel naoPanel;
    private ActionRecordPostion actionRecordPosition;
    private JToolBar toolBar;
    private PropertyPanel propPanel;

    private File curFile; 

    /** frames in this sessions. */
    private final LinkedListModel<NaoFrame> frames;

    /** contains undo steps. */
    private final LinkedList<IAppMemento> undoList;

    /** contains redo steps. */
    private final LinkedList<IAppMemento> redoList;

    private final List<IUndoRedoListener> listenerUndoRedo;

    public static Injector injector;
	public static INaoCommunication comm;
	private ActionPlayCurrentFrame actionPlayCurrentFrame;
	private ActionPlayAnim actionPlayAnim;
	
	private IFrameTransformationStrategy standardStrategy;
	private ActionSave actionSave;
	private ActionOpen actionOpen;
	private JFrame mainWindow;
	private ActionCreateCalcKeyFrame actionCreateCalcKeyFrame;
	private ActionDeleteFrame actionDelFrame;

	private boolean isInitialized;
	private ActionMoveFrameUp actionMoveUp;
	private ActionMoveFrameDown actionMoveDown;
	private ActionDuplicateFrame actionDuplicateFrame;
	private ActionStand actionStand;
	private ActionUpdateFrame actionUpdatePosition;
	private ActionFollowFrame actionFollowFrame;
	private ActionSaveAs actionSaveAs;
	private ActionMirrorFrame actionMirrorFrame;
	private ActionMotionExport actionMotionExport;

	public boolean isInitialized() {
    	return isInitialized;
    }

    public JFrame getMainWindow() {
		return mainWindow;
	}

	public MainApplication() {
        frames = new LinkedListModel<NaoFrame>();
        undoList = new LinkedList<IAppMemento>();
        redoList = new LinkedList<IAppMemento>();
        listenerUndoRedo = new LinkedList<IUndoRedoListener>();
    }

    /**
     * Initialize the Look'n'Feel for the Application
     */
    protected void initializeLookAndFeel() {
        try {
            if (SystemUtils.IS_OS_WINDOWS) {
                UIManager.setLookAndFeel("com.jgoodies.looks.windows.WindowsLookAndFeel");
            } else if (SystemUtils.IS_OS_MAC) {
                // do nothing, use the Mac Aqua L&f
            } else {
                UIManager.setLookAndFeel("org.pushingpixels.substance.api.skin.SubstanceModerateLookAndFeel");
            }
        } catch (final Exception e) {
            // Likely the Looks library is not in the class path; ignore.
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create and initialize the main window frame.
     */
    protected JFrame createMainFrame(String title) {
        final JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        return frame;
    }

    protected final void packAndShowOnScreenCenter(JFrame frame) {
        frame.pack();

        final Dimension paneSize = frame.getSize();
        final Dimension screenSize = frame.getToolkit().getScreenSize();
        frame.setLocation((screenSize.width - paneSize.width) / 2,
                (int) ((screenSize.height - paneSize.height) * 0.45));

        isInitialized = true;
        frame.setVisible(true);
    }

    /**
     * Constructs all components used in the window
     */
    protected void initComponents() {
        naoPanel = new NaoPanel();

        frameList = new JList<NaoFrame>();
        frameList.setModel(frames);
        final Color c = frameList.getBackground().darker().darker();
        frameList.setCellRenderer(new ListFrameRenderer());
        frameList.addListSelectionListener(new ListSelectionListener() {

            public void valueChanged(ListSelectionEvent e) {
            	
            	/* importing to only execute this one time! */
            	if(e.getValueIsAdjusting()) {
					return;
				}
            	
                final Object obj = frameList.getSelectedValue();

                if (obj == null) {
                	actionDelFrame.setEnabled(false);
                	actionDuplicateFrame.setEnabled(false);
                	actionCreateCalcKeyFrame.setEnabled(false);
                	actionPlayCurrentFrame.setEnabled(false);
                	actionMoveDown.setEnabled(false);
                	actionMoveUp.setEnabled(false);
                	actionMirrorFrame.setEnabled(false);
                    return;
                }

            	actionDelFrame.setEnabled(true);
            	actionDuplicateFrame.setEnabled(true);
            	actionCreateCalcKeyFrame.setEnabled(true);
            	actionPlayCurrentFrame.setEnabled(true);
            	actionMirrorFrame.setEnabled(true);
            	
            	int index = frameList.getSelectedIndex();
        		actionMoveUp.setEnabled(index > 0);
        		actionMoveDown.setEnabled(index < frames.size() - 1);
            	
                final NaoFrame frame = (NaoFrame) obj;
                naoPanel.setFrame(frame);
                propPanel.setFrame(frame);
                
                if(actionFollowFrame.getValue(Action.SELECTED_KEY).equals(Boolean.TRUE)) {
                	try {
						frame.moveSlowlyTo(comm);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
                }
                
            }
        });

        final JScrollPane jScrollPane = new JScrollPane(frameList);
        jScrollPane.setBorder(NaoBorderFactory.getInnerFrameBorder());
        frameListContainer = new JXTitledPanel("Key Frames", jScrollPane);
        frameListContainer.setBorder(BorderFactory.createLineBorder(c));

        naoOverviewContainer = new JXTitledPanel("Nao Overview", naoPanel.getContentPanel());
        naoOverviewContainer.setBorder(BorderFactory.createLineBorder(c));

        propPanel = new PropertyPanel(this);
        propContainer = new JXTitledPanel("Properties", propPanel.getContentPanel());
        propContainer.setBorder(BorderFactory.createLineBorder(c));
    }
   
    /**
     * Create actions needed in editor
     */
    protected void initActions() {
        actionRecordPosition = injector.getInstance(ActionRecordPostion.class);
        actionRecordPosition.setComm(comm);
        actionUpdatePosition = injector.getInstance(ActionUpdateFrame.class);
        actionUpdatePosition.setComm(comm);
        
        actionStand = injector.getInstance(ActionStand.class);
        actionStand.setComm(comm);

        actionPlayCurrentFrame = injector.getInstance(ActionPlayCurrentFrame.class);
        actionFollowFrame = injector.getInstance(ActionFollowFrame.class);
        actionPlayCurrentFrame.setComm(comm);

        actionPlayAnim = injector.getInstance(ActionPlayAnim.class);
        actionPlayAnim.setComm(comm);

        actionDelFrame = injector.getInstance(ActionDeleteFrame.class);

        actionDuplicateFrame = injector.getInstance(ActionDuplicateFrame.class);
        actionDuplicateFrame.setFrameList(frameList);
        actionMirrorFrame = injector.getInstance(ActionMirrorFrame.class);
        actionMirrorFrame.setFrameList(frameList);
        actionMoveUp = injector.getInstance(ActionMoveFrameUp.class);
        actionMoveUp.setFrameList(frameList);
        actionMoveDown = injector.getInstance(ActionMoveFrameDown.class);
        actionMoveDown.setFrameList(frameList);
        
        actionCreateCalcKeyFrame = injector.getInstance(ActionCreateCalcKeyFrame.class);
        actionCreateCalcKeyFrame.setComm(comm);
        
        actionSave = injector.getInstance(ActionSave.class);
        actionSaveAs = injector.getInstance(ActionSaveAs.class);
        actionMotionExport = injector.getInstance(ActionMotionExport.class);
        actionMotionExport.setComm(comm);
        actionOpen = injector.getInstance(ActionOpen.class);

        fireUndoRedoListener();
        
        actionDuplicateFrame.setEnabled(false);
    	actionDelFrame.setEnabled(false);
    	actionMoveUp.setEnabled(false);
    	actionMoveDown.setEnabled(false);
    	actionCreateCalcKeyFrame.setEnabled(false);
    	actionPlayCurrentFrame.setEnabled(false);
    	actionRecordPosition.setEnabled(false);
    	actionUpdatePosition.setEnabled(false);
    	actionMirrorFrame.setEnabled(false);
    }

    /**
     * Create the toolbar.
     *
     * @return
     */
    protected void configureToolbar(JToolBar toolBar) {
    	toolBar.add(actionOpen);
    	toolBar.add(actionSave);
    	toolBar.add(actionSaveAs);
    	toolBar.add(actionMotionExport);    	
        toolBar.addSeparator();
        toolBar.add(actionStand);
        toolBar.add(actionPlayAnim);        
        toolBar.addSeparator();
        toolBar.add(actionDelFrame);
        toolBar.add(actionMoveUp);
        toolBar.add(actionMoveDown);
        toolBar.add(actionDuplicateFrame);
        toolBar.add(actionMirrorFrame);
        toolBar.addSeparator();
        toolBar.add(actionRecordPosition);
        toolBar.add(actionUpdatePosition);
        toolBar.add(actionCreateCalcKeyFrame);
        toolBar.addSeparator();
        toolBar.add(actionPlayCurrentFrame);
        
        JToggleButton btn = new JToggleButton(actionFollowFrame);
        toolBar.add(btn);
    }

    /**
     * add all components to a layout
     *
     * @return the build component
     */
    protected JComponent buildPanel() {
        final FormLayout mainLayout = new FormLayout(
                "fill:60dlu, $lcgap, fill:pref:grow, $lcgap, fill:[250dlu, pref]",
                "fill:[300dlu,pref]:grow");

        final int FIRST_COL = 1;
        final int SECOND_COL = 3;
        final int THIRD_COL = 5;

        final CellConstraints cc = new CellConstraints();

        final PanelBuilder builder = new PanelBuilder(mainLayout /*
                                                            * , new
                                                            * FormDebugPanel
                                                            * (mainLayout)
                                                            */);
        builder.setDefaultDialogBorder();
        builder.add(frameListContainer, cc.xy(FIRST_COL, 1));
        builder.add(propContainer, cc.xy(SECOND_COL, 1));
        builder.add(naoOverviewContainer, cc.xy(THIRD_COL, 1));

        /* extra panel for toolbar */
        final JPanel borderLayoutPanel = new JPanel(new BorderLayout());
        toolBar = new JToolBar();
        borderLayoutPanel.add(toolBar, BorderLayout.PAGE_START);
        borderLayoutPanel.add(builder.getPanel(), BorderLayout.CENTER);

        return borderLayoutPanel;
    }

    public void launch() {
		initializeLookAndFeel();
		initComm();
    	
		standardStrategy = new ScriptingTransformationStrategy();

        /* separates construction from layouting */
        initComponents();

        mainWindow = createMainFrame("Nao Motion Editor");
        mainWindow.getContentPane().add(buildPanel());
        initActions();

        configureToolbar(toolBar);
        packAndShowOnScreenCenter(mainWindow);

        naoPanel.setFrame(new NaoFrame(NaoConstants.STAND_ANGLES));
        frames.add(new NaoFrame(NaoConstants.STAND_ANGLES));
    }

	private void initComm() {
		comm = injector.getInstance(INaoCommunication.class);
    	Preferences prefs = Preferences.userNodeForPackage(getClass());
    	while(!comm.isConnected()) {
    		String ip = (String) JOptionPane.showInputDialog(null, 
    				"IP Adress of the Nao", "Nao IP Adress Needed", 
    				JOptionPane.QUESTION_MESSAGE, null, null, 
    				prefs.get(PREF_IP_ADRESS, ""));
    		
    		if(ip == null) {
				System.exit(0);
			}
    		
	    	try {
				comm.connect(ip, 51385);
				prefs.put(PREF_IP_ADRESS, ip);
				prefs.flush();
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, 
						"Can't connect to Nao. Right IP adress? Does the motion editor strategy run?", 
						"Connection Error", JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
			}
    	}
	}

    public static void main(String[] args) {
		/* warm up scripting engine in background */
		Thread t = new Thread(new Runnable() {
			
			public void run() {
		    	ScriptEngineManager sm = new ScriptEngineManager();
		    	ScriptEngine se = sm.getEngineByName("groovy");
		    	try {
					se.eval("println \"I'm hot baby\"");
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
		});
		t.start();

        injector = Guice.createInjector(new NaoModule());
        final MainApplication app = injector.getInstance(MainApplication.class);
        
    	SwingUtilities.invokeLater(new Runnable() {
			public void run() {
		        app.launch();
			}
		});
    }

    /**
     * @return the body parts where the stiffness is reduced.
     */
    public List<BodyPart> getCurrentSelectedBodyParts() {
        return naoPanel.getSelectedBodyParts();
    }

    /**
     * @return the frame which is currently selected in the list.
     */
    public NaoFrame getCurrentSelectedFrame() {
        NaoFrame frame = frameList.getSelectedValue();

        if(frame == null) {
            frame = NaoFrame.EMPTY_FRAME;
        }

        return frame;
    }

    public int getCurrentSelectedFrameIndex() {
        return frameList.getSelectedIndex();
    }

    /**
     * Creates a memento rich represent the current application state
     *
     * @return the created memento
     */
    public IAppMemento createAppMemento() {
        final AppMementoBuilder builder = new AppMementoBuilder();
        builder.setFrames(frames);
        builder.setSelectedFrame(getCurrentSelectedFrame());

        final IAppMemento mem = builder.createMemento();
        undoList.add(mem);
        redoList.clear();
        return mem;
    }

    public void createUndoStep() {
        final IAppMemento mem = createAppMemento();
        undoList.push(mem);
        fireUndoRedoListener();
    }


    /**
     * Undo the last saved operation.
     */
    public void undo() {
        if (undoList.isEmpty()) {
            return;
        }

        final IAppMemento mem = undoList.pop();
        redoList.push(mem);
        restoreState(mem);
        fireUndoRedoListener();
    }

    /**
     * Redo the last saved operation.
     */
    public void redo() {
        if (redoList.isEmpty()) {
            return;
        }

        final IAppMemento mem = redoList.pop();
        undoList.push(mem);
        restoreState(mem);
        fireUndoRedoListener();
    }

    /**
     * Restore the Application state from a memento.
     *
     * @param mem
     *            the state to restore.
     */
    protected void restoreState(IAppMemento mem) {
        frames.clear();
        frames.addAll(mem.getFrames());

        NaoFrame frame = null;

        if(mem.getSelectedFrame() != null) {
            frame = mem.getSelectedFrame();
        } else {
            if(frames.size() > 0) {
                frame = frames.getFirst();
            } else {
                frame = NaoFrame.EMPTY_FRAME;
            }
        }

        frameList.setSelectedValue(frame, true);
        naoPanel.setFrame(frame);

    }
    
    public void setFrameList(List<NaoFrame> list) {
    	frames.clear();
    	frames.addAll(list);
    	
        NaoFrame frame = null;
        if(frames.size() > 0) {
            frame = frames.getFirst();
        } else {
            frame = NaoFrame.EMPTY_FRAME;
        }
        
        naoPanel.setFrame(frame);
        
        redoList.clear();
        undoList.clear();

    }

    /**
     * @return the current frame list
     */
    public LinkedListModel<NaoFrame> getFrameList() {
        return frames;
    }

    /**
     * Add a listener which is notified when the undo or redo state change.
     *
     * @param listener
     *            listener to add
     */
    public void addUndoRedoListener(IUndoRedoListener listener) {
        listenerUndoRedo.add(listener);
    }

    /**
     * Remove a listener which is notified when the undo or redo state change.
     *
     * @param listener
     *            listener to add
     */
    public void removeUndoRedoListener(IUndoRedoListener listener) {
        listenerUndoRedo.remove(listener);
    }

    public void fireUndoRedoListener() {
        for (final IUndoRedoListener listener : listenerUndoRedo) {
            listener.undoRedoStateChanged(!undoList.isEmpty(), !redoList.isEmpty());
        }
    }

	public void setStandardStrategy(IFrameTransformationStrategy standardStrategy) {
		this.standardStrategy = standardStrategy;
		propPanel.changedStandardStrategy();
	}

	public IFrameTransformationStrategy getStandardStrategy() {
		return standardStrategy;
	}

	public void setCurFile(File f) {
		this.curFile = f;
	}

	public File getCurFile() {
		return curFile;
	}

	public void setEnableRecordAction(boolean b) {
		actionRecordPosition.setEnabled(b);
		actionUpdatePosition.setEnabled(b);
		
	}

	public boolean isStiff() {
		return naoPanel.isStiff();
	}

	public void setSelectedFrame(NaoFrame frame) {
		frameList.setSelectedValue(frame, true);
	}
}
