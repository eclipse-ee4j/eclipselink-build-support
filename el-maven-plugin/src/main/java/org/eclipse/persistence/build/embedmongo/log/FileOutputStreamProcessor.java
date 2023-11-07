/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.build.embedmongo.log;

import de.flapdoodle.embed.process.io.StreamProcessor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class FileOutputStreamProcessor implements StreamProcessor {

    private static OutputStreamWriter stream;

    private String logFile;
    private String encoding;

    public FileOutputStreamProcessor(String logFile, String encoding) {
        setLogFile(logFile);
        setEncoding(encoding);
    }

    @Override
    public synchronized void process(String block) {
        try {

            if (stream == null) {
                stream = new OutputStreamWriter(new FileOutputStream(logFile), encoding);
            }

            stream.write(block);
            stream.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onProcessed() {
        process("\n");
    }

    private void setLogFile(String logFile) {
        if (logFile == null || logFile.trim().length() == 0) {
            throw new IllegalArgumentException("no logFile given");
        }
        this.logFile = logFile;
    }

    private void setEncoding(String encoding) {
        if (encoding == null || encoding.trim().length() == 0) {
            throw new IllegalArgumentException("no encoding given");
        }
        this.encoding = encoding;
    }
}
