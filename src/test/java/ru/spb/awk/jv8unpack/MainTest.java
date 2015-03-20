package ru.spb.awk.jv8unpack;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.zip.DataFormatException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class MainTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testMain()  {
		String[] argv = {"-u","src/test/resources/Обмен.epf","src/test/resources/Обмен_epf"};
		try {
			Main.main(argv);
		} catch (DataFormatException | IOException e) {
			e.printStackTrace();
			throw new Error(e);
		}
	}

}
