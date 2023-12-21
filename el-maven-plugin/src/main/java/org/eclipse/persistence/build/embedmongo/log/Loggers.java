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

import de.flapdoodle.embed.process.io.ProcessOutput;
import de.flapdoodle.embed.process.io.NamedOutputStreamProcessor;

public class Loggers {

    public enum LoggingOutput {
        FILE, CONSOLE, NONE
    }

    public static ProcessOutput file(String logFile, String encoding) {
        FileOutputStreamProcessor file = new FileOutputStreamProcessor(logFile, encoding);
        return  ProcessOutput.builder()
                .output(new NamedOutputStreamProcessor("[mongod output]", file))
                .error(new NamedOutputStreamProcessor("[mongod error]", file))
                .commands(new NamedOutputStreamProcessor("[mongod commands]", file))
                .build();
    }

    public static ProcessOutput console() {
        return ProcessOutput.namedConsole("");
    }

    public static ProcessOutput none() {
        return ProcessOutput.silent();
    }
}
