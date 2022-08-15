package org.ptg.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;
import org.ptg.events.StringEvent;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import com.google.common.collect.Maps;

public class AutomateUtils {
	public static Map<String, String> passwords = Maps.newHashMap();
	public static Map<String, String> users = Maps.newHashMap();
	public static Map<String, String> hosts = Maps.newHashMap();
	static {
		passwords.put("local1", "");
		users.put("local1", "maverick");
		hosts.put("local1", "192.168.56.101");

		users.put("giant", "maverick");
		passwords.put("giant", "");
		hosts.put("giant", "192.168.1.14");

	}

	public static boolean antScpTo(String file, String toDir, String context) {
		Project p = new Project();
		p.setBaseDir(new File("."));
		Scp scp = new Scp();
		scp.setProject(p);
		scp.setFile(file);
		scp.setPassword(AutomateUtils.passwords.get(context));
		String toDirPath = AutomateUtils.users.get(context) + "@" + AutomateUtils.hosts.get(context) + ":" + toDir;
		scp.setTodir(toDirPath);
		scp.setTrust(true);
		scp.execute();
		return true;
	}

	public static boolean runScript(String context, String code) {
		try {
			Connection conn = new Connection(AutomateUtils.hosts.get(context), 22);
			conn.connect();
			boolean isAuthenticated = conn.authenticateWithPassword(AutomateUtils.users.get(context), AutomateUtils.passwords.get(context));
			if (isAuthenticated == false) {
				throw new IOException("Authentication failed.");
			}
			Session sess = conn.openSession();
			sess.execCommand(code);
			InputStream stdout = new StreamGobbler(sess.getStdout());
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			StringBuilder sb = new StringBuilder();
			while (true) {
				String line = br.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
			}
			br.close();
			sess.close();
			conn.close();
			System.out.println("REsult: " + sb.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("RunScript : " + code);
		return false;
	}

	public static boolean echo(String txt) {
		System.out.println("echo: " + txt);
		return false;
	}

	public static boolean sendToUi(String s) {
		ContPublisherWriter.getInstance().loadEvent(new StringEvent(s));
		return true;
	}

	public static boolean sendToUi(Object o) {
		if (o != null) {
			ContPublisherWriter.getInstance().loadEvent(new StringEvent(o.toString()));
			return true;
		}
		return false;
	}

	public boolean runGraph(String name) {
		Class cls = null;
		try {
			cls = CommonUtil.forName("Mapping_" + name);
			try {
				Object o = cls.newInstance();
				if (o instanceof IRunnable) {
					IRunnable r = (IRunnable) o;
					r.run();
				} else if (o instanceof org.apache.commons.collections.Closure) {
					org.apache.commons.collections.Closure c = (Closure) o;
					c.execute(null);
				} else if (o instanceof IOpencvProcessor) {
					final IOpencvProcessor p = (IOpencvProcessor) o;
					new Thread(new Runnable() {
						@Override
						public void run() {
							p.restart();
							p.process();
						}
					}).start();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			Object o = cls.newInstance();
			if (o instanceof IRunnable) {
				IRunnable r = (IRunnable) o;
				r.run();
			} else if (o instanceof org.apache.commons.collections.Closure) {
				org.apache.commons.collections.Closure c = (Closure) o;
				c.execute(null);
			} else if (o instanceof IOpencvProcessor) {
				final IOpencvProcessor p = (IOpencvProcessor) o;
				new Thread(new Runnable() {
					@Override
					public void run() {
						p.restart();
						p.process();
					}
				}).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
