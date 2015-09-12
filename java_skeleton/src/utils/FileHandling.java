package utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import oidc_rp.Client;

public class FileHandling {

	/**
	 * Read all data from a file.
	 *
	 * @param path
	 *            path of the file
	 * @return All data from the file.
	 * @throws IOException
	 */
	public static String readFromFile(String path) throws IOException {
		return new String(Files.readAllBytes(Client.ROOT_PATH.resolve(Paths
				.get(path))), StandardCharsets.UTF_8);
	}
}
