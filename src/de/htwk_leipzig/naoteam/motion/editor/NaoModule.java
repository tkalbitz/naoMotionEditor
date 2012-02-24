/*******************************************************************************
 * Copyright (c) 2010, 2011, 2012 Tobias Kalbitz <tobias.kalbitz@googlemail.com>
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package de.htwk_leipzig.naoteam.motion.editor;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

import de.htwk_leipzig.naoteam.motion.editor.communication.INaoCommunication;
import de.htwk_leipzig.naoteam.motion.editor.communication.internal.DummyNaoCommunicationImpl;
import de.htwk_leipzig.naoteam.motion.editor.communication.internal.NaoCommunicationImpl;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.TransformationFactory;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.interfaces.IFrameTransformationConfigBuilder;
import de.htwk_leipzig.naoteam.motion.editor.frame.transformation.internal.FrameTransformationBuilderImpl;
import de.htwk_leipzig.naoteam.motion.editor.gui.IImageLoader;
import de.htwk_leipzig.naoteam.motion.editor.gui.MainApplication;
import de.htwk_leipzig.naoteam.motion.editor.gui.internal.ImageLoaderImpl;

public class NaoModule extends AbstractModule {

    @Override
    protected void configure() {
    	String boolStr = System.getProperty("USE_DUMMY_COMM", "false");
    	
    	if(Boolean.parseBoolean(boolStr)) {
			bind(INaoCommunication.class).to(DummyNaoCommunicationImpl.class).in(Scopes.SINGLETON);
		} else {
			bind(INaoCommunication.class).to(NaoCommunicationImpl.class).in(Scopes.SINGLETON);
		}
    	
        bind(IImageLoader.class).to(ImageLoaderImpl.class).in(Scopes.SINGLETON);
        bind(TransformationFactory.class).in(Scopes.SINGLETON);
        bind(MainApplication.class).in(Scopes.SINGLETON);
        bind(IFrameTransformationConfigBuilder.class).to(FrameTransformationBuilderImpl.class);
    }
}
