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
package org.jboss.aesh.readline;

import org.jboss.aesh.parser.Parser;
import org.jboss.aesh.readline.editing.EditModeBuilder;
import org.jboss.aesh.readline.history.InMemoryHistory;
import org.jboss.aesh.tty.Connection;
import org.jboss.aesh.tty.Signal;
import org.jboss.aesh.tty.Size;
import org.junit.Test;

import java.io.IOException;
import java.util.function.Consumer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:stale.pedersen@jboss.org">Ståle W. Pedersen</a>
 */
public class ConsoleBufferTest {

    private ConsoleBuffer createConsoleBuffer(Connection connection) {
       return new AeshConsoleBuffer(connection, new Prompt("[aesh@rules]: "), EditModeBuilder.builder().create(),
                new InMemoryHistory(50), null, connection.size(), true);
    }

    private ConsoleBuffer createConsoleBuffer(Connection connection, String prompt) {
       return new AeshConsoleBuffer(connection, new Prompt(prompt), EditModeBuilder.builder().create(),
                new InMemoryHistory(50), null, connection.size(), true);
    }

    @Test
    public void testSimpleWrites() throws IOException {
        SimpleConnection connection = new SimpleConnection();
        ConsoleBuffer consoleBuffer = createConsoleBuffer(connection);

        consoleBuffer.drawLine();
        assertTrue(connection.bufferBuilder.toString().contains("aesh"));
        connection.bufferBuilder.delete(0, connection.bufferBuilder.length());
        //byteArrayOutputStream.reset();

        consoleBuffer.writeString("foo");
        assertEquals("foo", connection.bufferBuilder.toString());


        consoleBuffer.writeString("OOO");
        assertEquals("fooOOO", connection.bufferBuilder.toString());
    }

    @Test
    public void testMovement()  throws IOException {

        SimpleConnection connection = new SimpleConnection();
        ConsoleBuffer consoleBuffer = createConsoleBuffer(connection,"");

        consoleBuffer.writeString("foo0");
        consoleBuffer.moveCursor(-1);
        assertEquals("foo0" + new String(BufferString.printAnsi("1D")), connection.bufferBuilder.toString());
        consoleBuffer.moveCursor(-10);
        assertEquals("foo0" + new String(BufferString.printAnsi("1D")) + new String(BufferString.printAnsi("2D")), connection.bufferBuilder.toString());

        consoleBuffer.writeString("1");
        assertEquals("1foo0", consoleBuffer.getBuffer().asString());

        connection.bufferBuilder.delete(0, connection.bufferBuilder.length());
        consoleBuffer.moveCursor(1);
        assertEquals(new String(BufferString.printAnsi("1C")), connection.bufferBuilder.toString());

        consoleBuffer.writeString("2");
        assertEquals("1f2oo0", consoleBuffer.getBuffer().asString());
    }

    class SimpleConnection implements Connection {

        private Consumer<Size>  sizeHandler;
        private Consumer<int[]> stdOutHandler;
        private StringBuilder bufferBuilder;

        SimpleConnection() {

            bufferBuilder = new StringBuilder();
            stdOutHandler = ints -> {
                bufferBuilder.append(Parser.fromCodePoints(ints));
            };
        }

        public String getBuffer() {
            return bufferBuilder.toString();
        }

        @Override
        public String terminalType() {
            return null;
        }

        @Override
        public Size size() {
            return new Size(80, 20);
        }

        @Override
        public Consumer<Size> getSizeHandler() {
            return null;
        }

        @Override
        public void setSizeHandler(Consumer<Size> handler) {

        }

        @Override
        public Consumer<Signal> getSignalHandler() {
            return null;
        }

        @Override
        public void setSignalHandler(Consumer<Signal> handler) {

        }

        @Override
        public Consumer<int[]> getStdinHandler() {
            return null;
        }

        @Override
        public void setStdinHandler(Consumer<int[]> handler) {

        }

        @Override
        public Consumer<int[]> stdoutHandler() {
            return stdOutHandler;
        }

        @Override
        public void setCloseHandler(Consumer<Void> closeHandler) {

        }

        @Override
        public Consumer<Void> getCloseHandler() {
            return null;
        }

        @Override
        public void close() {

        }
    }

}
