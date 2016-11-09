/*
 * JBoss, Home of Professional Open Source
 * Copyright 2014 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aesh.readline.action.mappings;

import org.aesh.readline.ConsoleBuffer;
import org.aesh.readline.InputProcessor;
import org.aesh.readline.action.Action;

import java.util.Arrays;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class DeletePrevChar implements Action {
    @Override
    public String name() {
        return "backward-delete-char";
    }

    @Override
    public void accept(InputProcessor inputProcessor) {
        if(inputProcessor.getBuffer().getBuffer().isMasking()) {
            if(inputProcessor.getBuffer().getBuffer().getPrompt().getMask() == 0) {
                deleteWithMaskEnabled(inputProcessor.getBuffer());
                return;
            }
        }
        deleteNoMasking(inputProcessor.getBuffer());
    }

    private void deleteNoMasking(ConsoleBuffer consoleBuffer) {
        //int cursor = consoleBuffer.getBuffer().getMultiCursor();
        int cursor = consoleBuffer.getBuffer().getCursor();
        if(cursor > 0) {
            int lineSize = consoleBuffer.getBuffer().length();
            if(cursor > lineSize)
                cursor = lineSize;

            consoleBuffer.addActionToUndoStack();
            consoleBuffer.getPasteManager().addText(Arrays.copyOfRange(consoleBuffer.getBuffer().getMultiLine(), cursor-1, cursor));
            consoleBuffer.delete(-1);
        }
    }

    private void deleteWithMaskEnabled(ConsoleBuffer consoleBuffer) {
        if(consoleBuffer.getBuffer().length() > 0) {
            consoleBuffer.delete(-1);
            consoleBuffer.moveCursor(consoleBuffer.getBuffer().length());
        }
    }
}