package com.sqiwy.dashboard.logger;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;

import com.google.gson.Gson;
import com.squareup.tape.FileObjectQueue;

/**
 * Created by abrysov
 */

public class LoggerQueue extends FileObjectQueue<LogMessage>{

	/**
	 * 
	 * @param file
	 * @param converter
	 * @throws IOException
	 */
	public LoggerQueue(File file, FileObjectQueue.Converter<LogMessage> converter) throws IOException {
		
		super(file, converter);
	}
	
	/**
	 *
	 * @param <T>
	 */
	public static class GsonConverter<T> implements FileObjectQueue.Converter<T> {
		
		/**
		 * 
		 */
		private final Gson gson;
		private Class<T> type;

		/**
		 * 
		 * @param gson
		 * @param type
		 */
		public GsonConverter(Gson gson, Class<T> type) {
			
			this.gson = gson;
			this.type = type;
		}

		/**
		 * 
		 */
		@Override
		public T from(byte[] bytes) {
			
			Reader reader = new InputStreamReader(new ByteArrayInputStream(bytes));
			return gson.fromJson(reader, type);
		}

		/**
		 * 
		 */
		@Override
		public void toStream(T object, OutputStream bytes) throws IOException {
			
			Writer writer = new OutputStreamWriter(bytes);
			gson.toJson(object, writer);
			writer.close();
		}
	}
}
