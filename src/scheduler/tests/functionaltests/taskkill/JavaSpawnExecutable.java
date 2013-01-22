/*
 *  *
 * ProActive Parallel Suite(TM): The Java(TM) library for
 *    Parallel, Distributed, Multi-Core Computing for
 *    Enterprise Grids & Clouds
 *
 * Copyright (C) 1997-2011 INRIA/University of
 *                 Nice-Sophia Antipolis/ActiveEon
 * Contact: proactive@ow2.org or contact@activeeon.com
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; version 3 of
 * the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://proactive.inria.fr/team_members.htm
 *  Contributor(s):
 *
 *  * $$PROACTIVE_INITIAL_DEV$$
 */
package functionaltests.taskkill;

import org.apache.log4j.Level;
import org.ow2.proactive.scheduler.common.task.TaskResult;
import org.ow2.proactive.scheduler.common.task.executable.JavaExecutable;
import org.ow2.proactive.scheduler.util.process.OperatingSystem;
import org.ow2.proactive.scheduler.util.process.OperatingSystemFamily;
import org.ow2.proactive.scheduler.util.process.ProcessTreeKiller;
import org.ow2.proactive.scheduler.util.process.ThreadReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;


/**
 * JavaSpawnExecutable
 *
 * @author The ProActive Team
 */
public class JavaSpawnExecutable extends JavaExecutable {

    public static URL launchersDir = TestProcessTreeKiller.class.getResource("/functionaltests/executables");

    public static URL nativeLinuxExecLauncher = JavaSpawnExecutable.class
            .getResource("/functionaltests/executables/PTK_launcher.sh");

    public static URL nativeWindowsExecLauncher = JavaSpawnExecutable.class
            .getResource("/functionaltests/executables/PTK_launcher.bat");

    public static URL nativeLinuxExecLauncher2 = JavaSpawnExecutable.class
            .getResource("/functionaltests/executables/PTK_launcher2.sh");

    public static URL nativeWindowsExecLauncher2 = JavaSpawnExecutable.class
            .getResource("/functionaltests/executables/PTK_launcher2.bat");

    Integer sleep;

    @Override
    public Serializable execute(TaskResult... results) throws Throwable {

        org.apache.log4j.Logger.getLogger(ProcessTreeKiller.class).setLevel(Level.DEBUG);
        Process process = null;

        process = Runtime.getRuntime().exec(getNativeExecLauncher(false), null,
                new File(launchersDir.toURI()).getCanonicalFile());

        // redirect streams
        BufferedReader sout = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader serr = new BufferedReader(new InputStreamReader(process.getErrorStream()));

        Thread tsout = new Thread(new ThreadReader(sout, System.out, this));
        Thread tserr = new Thread(new ThreadReader(serr, System.err, this));
        tsout.setDaemon(true);
        tserr.setDaemon(true);
        tsout.start();
        tserr.start();

        process.waitFor();

        Thread.sleep(sleep * 10000); // we sleep 2 sec

        return true;
    }

    public static String[] getNativeExecLauncher(boolean alternate) throws Exception {
        String osName = System.getProperty("os.name");
        OperatingSystem operatingSystem = OperatingSystem.resolveOrError(osName);
        OperatingSystemFamily family = operatingSystem.getFamily();
        String[] nativeExecLauncher = null;
        switch (family) {
            case LINUX:
            case UNIX:
            case MAC:
                String executable = null;
                if (alternate) {
                    executable = new File(nativeLinuxExecLauncher2.toURI()).getName();
                } else {
                    executable = new File(nativeLinuxExecLauncher.toURI()).getName();
                }
                // TODO runAsMe mode for this Test
                nativeExecLauncher = new String[] { "/bin/sh", executable };

                break;
            case WINDOWS:
                if (alternate) {
                    //nativeExecLauncher = new String[] { "cmd.exe", "/C", new File(TestProcessTreeKiller.nativeWindowsExecLauncher.toURI()).getName(),">", UUID.randomUUID().toString()+".txt"};
                    nativeExecLauncher = new String[] { "cmd.exe", "/C",
                            new File(nativeWindowsExecLauncher2.toURI()).getName() };
                } else {
                    nativeExecLauncher = new String[] { "cmd.exe", "/C",
                            new File(nativeWindowsExecLauncher.toURI()).getName() };

                }

        }
        return nativeExecLauncher;
    }

    public static void main(String[] args) throws Throwable {
        JavaSpawnExecutable jse = new JavaSpawnExecutable();
        jse.sleep = 1;
        jse.execute(new TaskResult[0]);
    }
}
