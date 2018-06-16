/**
 *	@copyright wanruome-2018
 * 	@author wanruome
 * 	@create 2018年6月16日 下午2:59:01
 */
package com.ruomm.base.requst;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class PostRepeatReaderRequestWrapper extends HttpServletRequestWrapper {
	private final byte[] body; // 报文

	public PostRepeatReaderRequestWrapper(HttpServletRequest request) throws IOException {
		super(request);
		body = readBytes(request.getInputStream());

	}

	@Override
	public ServletInputStream getInputStream() throws IOException {
		final ByteArrayInputStream bais = new ByteArrayInputStream(body);
		return new ServletInputStream() {

			@Override
			public int read() throws IOException {
				// TODO Auto-generated method stub
				return bais.read();
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				// TODO Auto-generated method stub

			}

			@Override
			public boolean isReady() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean isFinished() {
				// TODO Auto-generated method stub
				return false;
			}
		};

	}

	private static byte[] readBytes(InputStream in) throws IOException {
		BufferedInputStream bufin = new BufferedInputStream(in);
		final int buffSize = 1024;
		ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);
		byte[] temp = new byte[buffSize];
		int size = 0;
		while ((size = bufin.read(temp)) != -1) {
			out.write(temp, 0, size);
		}
		out.flush();
		byte[] content = out.toByteArray();
		bufin.close();
		out.close();
		return content;
	}

}
