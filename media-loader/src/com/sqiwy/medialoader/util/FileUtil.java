package com.sqiwy.medialoader.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.squareup.okhttp.OkHttpClient;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import android.os.Environment;
import android.provider.MediaStore;

/**
 * Created by abrysov
 */

public class FileUtil {
	
	private static final int EOF = -1;
	
	/**
     * The default buffer size ({@value}) to use for 
     * {@link #copyLarge(InputStream, OutputStream)}
     * and
     * {@link #copyLarge(Reader, Writer)}
     */
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	
	@SuppressWarnings("serial")
	public static class StorageException extends Exception {

		public StorageException() {
			super();
		}

		public StorageException(String detailMessage,
				Throwable throwable) {
			super(detailMessage, throwable);
		}

		public StorageException(String detailMessage) {
			super(detailMessage);
		}

		public StorageException(Throwable throwable) {
			super(throwable);
		}
		
	}
	
	private static boolean isStorageAvailable(String storageState) {
		return Environment.MEDIA_MOUNTED.equals(storageState)
				|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(storageState);
	}
	
	private static boolean isStorageWritable(String storageState) {
		return Environment.MEDIA_MOUNTED.equals(storageState);
	}
	
	/**
	 * Creates special file in the directory which will
	 * restrict media scanner from scanning directory for media.
	 * 
	 * @param dir directory where to create media ignore file.
	 */
	public static void createMediaIgnoreFile(File dir) throws StorageException {
		
		File nomediaFile = new File(dir, MediaStore.MEDIA_IGNORE_FILENAME);
		if (!nomediaFile.exists()) {
			try {
				nomediaFile.createNewFile();
			} catch (IOException e) {
				throw new StorageException(e);
			}
		}
	}
	
	public static File getStorage(boolean readOnly) throws StorageException {
		String state = Environment.getExternalStorageState();

		boolean mExternalStorageAvailable = isStorageAvailable(state);
		boolean mExternalStorageWriteable = isStorageWritable(state);
		
		if (!mExternalStorageAvailable) {
			throw new StorageException("Storage is not available.");
		}
		
		if (!readOnly && !mExternalStorageWriteable) {
			throw new StorageException("Storage is not writable.");
		}
		
		return Environment.getExternalStorageDirectory();
	}
	
	public static void checkDir(File dir) {
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				// TODO: handle in an appropriate way
				throw new IllegalStateException("Folder [" + dir.getName()
						+ "] doesn't exist");
			}
		}
	}
	
    /**
     * Copies bytes from the URL <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     *
     * @param source  the <code>URL</code> to copy bytes from, must not be {@code null}
     * @param destination  the non-directory <code>File</code> to write bytes to
     *  (possibly overwriting), must not be {@code null}
     * @param connectionTimeout the number of milliseconds until this method
     *  will timeout if no connection could be established to the <code>source</code>
     * @param readTimeout the number of milliseconds until this method will
     *  timeout if no data could be read from the <code>source</code> 
     * @throws IOException if <code>source</code> URL cannot be opened
     * @throws IOException if <code>destination</code> is a directory
     * @throws IOException if <code>destination</code> cannot be written
     * @throws IOException if <code>destination</code> needs creating but can't be
     * @throws IOException if an IO error occurs during copying
     * @since 2.0
     */
    public static void copyURLToFile(URL source, File destination,
            int connectionTimeout, int readTimeout, CancellationPolicy cancellation) throws IOException {

        OkHttpClient client = new OkHttpClient();
        HttpURLConnection connection = client.open(source);

        connection.setConnectTimeout(connectionTimeout);
        connection.setReadTimeout(readTimeout);
        InputStream input = connection.getInputStream();
        copyInputStreamToFile(input, destination, cancellation);
    }
    
    /**
     * Copies bytes from an {@link InputStream} <code>source</code> to a file
     * <code>destination</code>. The directories up to <code>destination</code>
     * will be created if they don't already exist. <code>destination</code>
     * will be overwritten if it already exists.
     *
     * @param source  the <code>InputStream</code> to copy bytes from, must not be {@code null}
     * @param destination  the non-directory <code>File</code> to write bytes to
     *  (possibly overwriting), must not be {@code null}
     * @throws IOException if <code>destination</code> is a directory
     * @throws IOException if <code>destination</code> cannot be written
     * @throws IOException if <code>destination</code> needs creating but can't be
     * @throws IOException if an IO error occurs during copying
     * @since 2.0
     */
    public static void copyInputStreamToFile(InputStream source, File destination, CancellationPolicy cancellation) throws IOException {
        try {
            FileOutputStream output = FileUtils.openOutputStream(destination);
            try {
                copy(source, output, cancellation);
                output.close(); // don't swallow close Exception if copy completes normally
            } finally {
                IOUtils.closeQuietly(output);
            }
        } finally {
            IOUtils.closeQuietly(source);
        }
    }
    
    // copy from InputStream
    //-----------------------------------------------------------------------
    /**
     * Copy bytes from an <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * Large streams (over 2GB) will return a bytes copied value of
     * <code>-1</code> after the copy has completed since the correct
     * number of bytes cannot be returned as an int. For large streams
     * use the <code>copyLarge(InputStream, OutputStream)</code> method.
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied, or -1 if &gt; Integer.MAX_VALUE
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 1.1
     */
    public static int copy(InputStream input, OutputStream output, CancellationPolicy cancellation) throws IOException {
        long count = copyLarge(input, output, cancellation);
        if (count > Integer.MAX_VALUE) {
            return -1;
        }
        return (int) count;
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method buffers the input internally, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     * The buffer size is given by {@link #DEFAULT_BUFFER_SIZE}.
     * 
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 1.3
     */
    public static long copyLarge(InputStream input, OutputStream output, CancellationPolicy cancellation)
            throws IOException {
        return copyLarge(input, output, new byte[DEFAULT_BUFFER_SIZE], cancellation);
    }

    /**
     * Copy bytes from a large (over 2GB) <code>InputStream</code> to an
     * <code>OutputStream</code>.
     * <p>
     * This method uses the provided buffer, so there is no need to use a
     * <code>BufferedInputStream</code>.
     * <p>
     *
     * @param input  the <code>InputStream</code> to read from
     * @param output  the <code>OutputStream</code> to write to
     * @param buffer the buffer to use for the copy
     * @return the number of bytes copied
     * @throws NullPointerException if the input or output is null
     * @throws IOException if an I/O error occurs
     * @since 2.2
     */
    public static long copyLarge(InputStream input, OutputStream output, byte[] buffer, CancellationPolicy cancellation)
            throws IOException {
        long count = 0;
        int n = 0;
        return count;
    }
}
