package org.app.util;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.app.user.UserContext;

public class Util {

	public static final byte[] EXPECTED_EVENT_DATA = "!".getBytes(UTF_8);
	public static final String EXPECTED_EVENT_NAME = "event";

	/*
	 * Once users are registered, the certificates needs to be stored
	 * in file system. The below line stores admin user's context in 
	 * the project's runtime folder. Certificates will subsequently
	 * be retrieved for interacting with the blockchain network
	 */

	public static void writeUserContext(UserContext userContext) throws Exception {
		String directoryPath = "users/" + userContext.getAffiliation();
		String filePath = directoryPath + "/" + userContext.getName() + ".ser";
		File directory = new File(directoryPath);
		if (!directory.exists()) {
			directory.mkdirs();
		}

		FileOutputStream file = new FileOutputStream(filePath);
		ObjectOutputStream out = new ObjectOutputStream(file);
		out.writeObject(userContext);
		out.close();
		file.close();
	}

	/*
	 * Read the stored user context based on username and affiliation
	 */
	public static UserContext readUserContext(String affiliation, String username) throws Exception {
		String filePath = "users/" + affiliation + "/" + username + ".ser";
		File file = new File(filePath);
		if (file.exists()) {
			FileInputStream fileStream = new FileInputStream(filePath);
			ObjectInputStream in = new ObjectInputStream(fileStream);
			UserContext uContext = (UserContext) in.readObject();
			in.close();
			fileStream.close();
			return uContext;
		} else {
			return null;
		}
	}

	/*
	 * Method to delete stored user contexts
	 */
	public static void cleanUp() {
		String directoryPath = "users";
		File directory = new File(directoryPath);
		deleteDirectory(directory);
	}

	public static boolean deleteDirectory(File dir) {
		if (dir.isDirectory()) {
			File[] children = dir.listFiles();

			for (int i = 0; i < children.length; ++i) {
				boolean success = deleteDirectory(children[i]);
				if (!success) {
					return false;
				}
			}
		}

		Logger.getLogger(Util.class.getName()).log(Level.INFO, "Deleting - " + dir.getName());
		return dir.delete();
	}
}
