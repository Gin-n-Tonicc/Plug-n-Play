package com.example.plug_n_play;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;

public class SmartFileAppender2 extends FileAppender {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private long maxFileSize = 10_000_000; // 10 MB
	private long maxDiskUsage = 10_000_000_000l; // 10 GB

	private LocalDate lastDate;
	private String givenFile;
	private String currentFileName;
	private String baseFileName;
	private String baseDirectoryPath;
	private String baseFileExtension;

	/**
	 * The default constructor simply calls its {@link FileAppender#FileAppender
	 * parents constructor}.
	 */
	public SmartFileAppender2() {
		super();
	}

	/**
	 * Instantiate a RollingFileAppender and open the file designated by
	 * <code>filename</code>. The opened filename will become the output destination
	 * for this appender.
	 * <p>
	 * If the <code>append</code> parameter is true, the file will be appended to.
	 * Otherwise, the file designated by <code>filename</code> will be truncated
	 * before being opened.
	 */
	public SmartFileAppender2(Layout layout, String filename, boolean append) throws IOException {
		super(layout, filename, append);
		this.givenFile = fileName;
	}

	/**
	 * Instantiate a FileAppender and open the file designated by
	 * <code>filename</code>. The opened filename will become the output destination
	 * for this appender.
	 * <p>
	 * The file will be appended to.
	 */
	public SmartFileAppender2(Layout layout, String fileName) throws IOException {
		this(layout, fileName, true);
	}

	/**
	 * Set maximum disk usage allowed for logging, before oldest files are deleted.
	 * Value under 50MB will be ignored
	 * @param maxDiskUsage Maximum Disk usage to be set
	 */
	public void setMaxDiskUsage(String maxDiskUsage) {
		long maxDiskUsageValue = OptionConverter.toFileSize(maxDiskUsage, this.maxDiskUsage);
		if (maxDiskUsageValue < 50_000_000) {
			return;
		}
		this.maxDiskUsage = maxDiskUsageValue;
	}

	/**
	 * Set maximum disk usage by current log file, before a new log file is created.
	 * This parameter may not be honored perfectly if logs generated per minute exceed this limit.
	 * Value under 1MB will be ignored
	 * @param maxFileSize Maximum allowed disk usage
	 */
	public void setMaxFileSize(String maxFileSize) {
		long maxFileSizeValue = OptionConverter.toFileSize(maxFileSize, this.maxFileSize);
		if (maxFileSizeValue < 1_000_000) {
			return;
		}
		this.maxFileSize = maxFileSizeValue;
	}

	/**
	 * This is called by super.setFile()
	 */
	@Override
	protected void setQWForFiles(Writer writer) {
		this.qw = new CountingQuietWriter(writer, errorHandler);
	}

	/**
	 * This method is called by super.constructor(
	 * @param fileName File to be used for logging. If already set to same file , do nothing
	 * @param append Append directly or truncate file first
	 * @param bufferedIO Whether to use buffering or not. This parameter is recommended to improve performance.
	 * @param bufferSize Bytes to buffer before flushing. This parameter can be left to default.
	 */
	@Override
	public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize)
			throws IOException {
		if (Objects.equals(fileName, currentFileName)) {
			return;
		}
		LogLog.warn("Setting new log file to: " + fileName);
		processGivenFileDetails(fileName);
		this.currentFileName = fileName;
		super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
		if (!append) {
			return;
		}
		File f = new File(fileName);
		getCountingQuietWriter().setCount(f.length());
	}

	@Override
	protected void subAppend(LoggingEvent event) {
		tryRollover();
		super.subAppend(event);
	}

	/**
	 * Checks if date has changed or max file size has been reached.
	 * If either is true, a new log file is created
	 * If Disk usage has maxed out, deletes oldest created files
	 * Synchronization not necessary since doAppend is already synced
	 */
	private void tryRollover() {
		// Check if date has changed
		LocalDate now = LocalDate.now();
		boolean isDateChanged = (lastDate == null || now.isAfter(lastDate));
		lastDate = now;

		// Check if file sized maxed out
		long size = getCountingQuietWriter().getCount();
		boolean isFileSizedMaxed = (size >= maxFileSize);

		// If date has not changed and file size is not mixed, return
		if (!isDateChanged && !isFileSizedMaxed) {
			return;
		}

		// Else create new log file
		String newFilename = getNewFilename();
		if (Objects.equals(newFilename, currentFileName)) {
			return;
		}

		try {
			setFile(newFilename, true, bufferedIO, bufferSize);
		} catch (IOException e) {
			if (e instanceof InterruptedIOException) {
				Thread.currentThread().interrupt();
			}
			LogLog.error("setFile(" + fileName + ", true) call failed.", e);
		}

		// Delete oldest files if disk size has exceeded
		long totalUsage = 0;
		File fileDirectory = new File(baseDirectoryPath);
		File[] logFiles = fileDirectory.listFiles(new MyFileFilter());
		Arrays.sort(logFiles, new FileModifiedDateComparator());
		for (File f : logFiles) {
			// Ignore current file from the total disk size
			if (Objects.equals(currentFileName, f.getPath())) {
				continue;
			}
			totalUsage += f.length();
			if (totalUsage < maxDiskUsage) {
				continue;
			}
			try {
				LogLog.warn("Deleting file: " + f.getPath());
				f.delete();
			} catch (SecurityException e) {
				LogLog.error("Error deleting file, file: " + f.getName(), e);
			}
		}
	}

	/**
	 * Generate name for log file based on date
	 * @return Generated file name (with baseFileExtension as extension)
	 */
	private String getNewFilename() {
		// Add time stamp to the filename
		LocalDateTime now = LocalDateTime.now();
		String timeStr = now.format(formatter);
		String newFileName = baseFileName + "." + timeStr + baseFileExtension;
		return Paths.get(baseDirectoryPath, newFileName).toString();
	}

	private CountingQuietWriter getCountingQuietWriter() {
		return (CountingQuietWriter) this.qw;
	}

	class MyFileFilter implements FileFilter {
		/**
		 * Checks if file name starts with baseFileName and has baseFileExtension extension
		 * @param f File to be checked
		 * @return True if matched, false otherwise
		 */
		public boolean accept(File f) {
			String fileName = f.getName();
			return fileName.startsWith(baseFileName) && fileName.endsWith(baseFileExtension);
		}
	}

	class FileModifiedDateComparator implements Comparator<File> {
		/**
		 * Compare files based on lastModified date
		 * @param o1 First filed to be compared
		 * @param o2 Second file to be compared
		 * @return Result of comparison
		 */
		public int compare(File o1, File o2) {
			return Long.compare(o2.lastModified(), o1.lastModified());
		}
	}

	/**
	 * Extract baseFile name, extension, path and other details from supplied file
	 * @param givenFile File to be considered as baseFile for analysis
	 */
	private void processGivenFileDetails(String givenFile) {
		if (this.givenFile != null) {
			return;
		}
		this.givenFile = givenFile;
		Path baseFilePath = Paths.get(givenFile);
		String fileName = baseFilePath.getFileName().toString();
		int i = fileName.indexOf(".");

		if (i != -1) {
			baseFileName = fileName.substring(0, i);
			baseFileExtension = fileName.substring(i);
		} else {
			baseFileName = fileName;
			baseFileExtension = "";
		}

		Path parent = baseFilePath.getParent();
		baseDirectoryPath = parent == null ? "." : parent.toString();

		LogLog.warn("GivenFile: " + givenFile);
		LogLog.warn("BaseDirectoryPath: " + baseDirectoryPath);
		LogLog.warn("BaseFileName: " + baseFileName);
		LogLog.warn("BaseFileExtension: " + baseFileExtension);
		LogLog.warn("MaxFileSize: " + maxFileSize);
		LogLog.warn("MaxDiskUsage: " + maxDiskUsage);
	}

}