package nl.avans.data;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.junit.Assert;

import com.opencsv.bean.CsvToBeanBuilder;

public class TestDataProvider {
	
	public static final TestDataProvider instance;

	static {
		instance = new TestDataProvider();
	}


	public <T> List<T> readTestData(String fileName, Class<T> clazz) throws Exception {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(fileName).getFile());
		String absolutePath = file.getAbsolutePath();

		Assert.assertTrue(absolutePath.endsWith(fileName));
		Assert.assertTrue(file.exists());

		return new CsvToBeanBuilder<T>(new FileReader(file))
			.withType(clazz)
			.withSeparator(';')
			.build()
			.parse();
	}
}
