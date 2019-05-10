package br.ufpe.cin.mergers.handlers;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ufpe.cin.app.JFSTMerge;
import br.ufpe.cin.files.FilesManager;
import br.ufpe.cin.mergers.util.MergeContext;

public class InitializationBlocksHandlerNewApproachTest {

	// TODO: check with Guilherme if/why this is needed
	@BeforeClass
	public static void setUpBeforeClass() throws UnsupportedEncodingException {
		//hidding sysout output
		@SuppressWarnings("unused")
		PrintStream originalStream = System.out;
		PrintStream hideStream    = new PrintStream(new OutputStream(){
			public void write(int b) {}
		},  true, Charset.defaultCharset().displayName());
		System.setOut(hideStream);
	}
}
