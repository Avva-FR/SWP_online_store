package kickstart.catalog;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.rules.TemporaryFolder;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@TestInstance(value = TestInstance.Lifecycle.PER_METHOD)
public class FileUploadUtilTest {
	@Rule
	TemporaryFolder tempFolder = new TemporaryFolder();

	@BeforeEach
	void prepare() throws IOException {
		tempFolder.create();
	}

	@Test
	void testSaveFile() throws IOException {
		File source = tempFolder.newFolder();
		MockMultipartFile testFile = new MockMultipartFile("test.txt", "test".getBytes());

		FileUploadUtil.saveFile(source.getAbsolutePath(), "test.txt", testFile);

		File file = new File(source, "test.txt");
		assertTrue(file.exists());
		assertEquals(Files.readString(Paths.get(file.toURI())), "test");
	}

	@Test
	void testFolderCreation() throws IOException {
		File source = tempFolder.newFolder();
		MockMultipartFile testFile = new MockMultipartFile("test.txt", "test".getBytes());

		FileUploadUtil.saveFile(source.getAbsolutePath() + File.pathSeparator + "baum", "test.txt", testFile);

		File folder = new File(source.getAbsolutePath() + File.pathSeparator + "baum");
		assertTrue(folder.exists());
		assertTrue(folder.isDirectory());

		File file = new File(folder, "test.txt");
		assertTrue(file.exists());
		assertEquals(Files.readString(Paths.get(file.toURI())), "test");
	}
}
