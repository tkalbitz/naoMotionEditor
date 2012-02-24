import de.htwk_leipzig.naoteam.motion.editor.frame.NaoFrame

if(frameCount > 0) {
    for (curFrame in 1..<frameCount) {
        for (joint in 0..<DOF) {
          float f = (float)curFrame/frameCount;
          newAc[joint] = (1-f)*fromAc[joint] + f*toAc[joint];
        }  
        frameList.add(new NaoFrame(newAc));
    }
}
