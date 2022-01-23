package urender.common.fs;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utilities for file operations.
 */
public class FSUtil {

	/**
	 * Removes a leading '/' from a file path, if present.
	 *
	 * @param path A file path, possibly prefixed with '/'.
	 * @return The input file path, with leading '/' removed.
	 */
	public static String cleanPathFromRootSlash(String path) {
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		return path;
	}

	
	/**
	 * Copies a disk File from one location to another, replacing any existing file at the location.
	 *
	 * @param source The file to copy.
	 * @param target The path to copy to.
	 */
	public static void copy(File source, File target) {
		try {
			Files.copy(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		} catch (IOException ex) {
			Logger.getLogger(FSUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Moves a disk File from one location to another, replacing any existing file at the location.
	 *
	 * @param source The file to move.
	 * @param target The path to move to.
	 */
	public static void move(File source, File target) {
		try {
			Files.move(source.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
		} catch (IOException ex) {
			Logger.getLogger(FSUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Writes an array of bytes into a disk File. Note that the non-native operation usually turns out to be faster.
	 *
	 * @param f File to write into.
	 * @param bytes The data to write.
	 */
	public static void writeBytesToFile(File f, byte[] bytes) {
		try {
			Files.write(f.toPath(), bytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException ex) {
			Logger.getLogger(FSUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Fully reads a disk file into a byte array.
	 *
	 * @param f The File to read.
	 * @return A byte array of the file data, or null if the operation failed.
	 */
	public static byte[] readFileToBytes(File f) {
		try {
			Path pth = f.toPath();
			return Files.readAllBytes(pth);
		} catch (IOException ex) {
			Logger.getLogger(FSUtil.class.getName()).log(Level.SEVERE, null, ex);
			return null;
		}
	}

	/**
	 * Reads an InputStream to a byte array in the fastest way possible. May result in undefined behavior if the stream's available() method does not produce accurate results (as it is permitted not to do so).
	 *
	 * @param strm An InputStream with an eligible available() method.
	 * @return A byte array containing all the remaining data in the input stream.
	 */
	public static byte[] readStreamToBytesFastAndDangerous(InputStream strm) {
		try {
			byte[] b = new byte[strm.available()];
			strm.read(b);
			strm.close();
			return b;
		} catch (IOException ex) {
			Logger.getLogger(FSUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * Writes an array of bytes to an OutputStream with handled IOExceptions.
	 *
	 * @param bytes The bytes to write.
	 * @param strm The OutputStream to write into.
	 */
	public static void writeBytesToStream(byte[] bytes, OutputStream strm) {
		try {
			strm.write(bytes);
			strm.close();
		} catch (IOException ex) {
			Logger.getLogger(FSUtil.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Creates a requested set of sub-directories in a directory.
	 *
	 * @param container The directory to create the sub-directories in.
	 * @param requiredContents Names of the sub-directories to be created.
	 */
	public static void mkDirsIfNotContains(File container, String... requiredContents) {
		List<String> contents = Arrays.asList(container.list());
		for (int i = 0; i < requiredContents.length; i++) {
			if (!contents.contains(requiredContents[i])) {
				new File(container.getAbsolutePath() + "/" + requiredContents[i]).mkdir();
			}
		}
	}

	/**
	 * Gets the pathname corresponding to a path's parent element.
	 *
	 * @param path A path.
	 * @return The parent of the path, or null if the path is a root path.
	 */
	public static String getParentFilePath(String path) {
		int end = path.replace('\\', '/').lastIndexOf("/");
		if (end != -1) {
			return path.substring(0, end);
		}
		return null;
	}

	/**
	 * Gets the file name in a pathname, AKA its last element.
	 *
	 * @param path A path.
	 * @return The last element of the path, possibly the entire path for root paths.
	 */
	public static String getFileName(String path) {
		int start = path.replace('\\', '/').lastIndexOf("/") + 1;
		return path.substring(start, path.length());
	}

	/**
	 * Gets the file extension of a file name, without the dot.
	 *
	 * @param fileName A file name.
	 * @return The extension of the file name, or an empty string if there is none.
	 */
	public static String getFileExtension(String fileName) {
		int lioDot = getLastDotIndexInName(fileName);
		return lioDot == -1 ? "" : fileName.substring(lioDot + 1);
	}

	/**
	 * Gets the file extension of a file name, including the dot.
	 *
	 * @param fileName A file name.
	 * @return The extension of the file name, or an empty string if there is none.
	 */
	public static String getFileExtensionWithDot(String fileName) {
		int lioDot = getLastDotIndexInName(fileName);
		return lioDot == -1 ? "" : fileName.substring(lioDot);
	}

	/**
	 * Gets the file name from a path, with the file extension removed.
	 *
	 * For example: - C:/Work/Stuff.txt -> Stuff - Sonic.exe -> Sonic
	 *
	 * @param fileName A file name or path.
	 * @return The input without the file extension.
	 */
	public static String getFileNameWithoutExtension(String fileName) {
		fileName = getFileName(fileName);
		int lioDot = getLastDotIndexInName(fileName);
		return lioDot != -1 ? fileName.substring(0, lioDot) : fileName;
	}
	
	public static String getFileNameWithoutExtension(String fileName, String extension) {
		if (fileName.endsWith(extension)) {
			return fileName.substring(0, fileName.length() - extension.length());
		}
		return fileName;
	}

	private static int getLastDotIndexInName(String fileName) {
		int slash = fileName.indexOf("/");
		int lioDot = fileName.lastIndexOf(".");
		return lioDot > slash ? lioDot : -1;
	}
}
