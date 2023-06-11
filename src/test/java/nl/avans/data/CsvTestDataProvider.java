package nl.avans.data;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.junit.Assert;

import com.opencsv.bean.CsvToBeanBuilder;

public class CsvTestDataProvider implements TestDataProvider {
	public <T> List<T> read(String name, Class<T> clazz) throws Exception {

		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(name + ".csv").getFile());
		Assert.assertTrue(file.exists());

		return new CsvToBeanBuilder<T>(new FileReader(file))
			.withType(clazz)
			.withSeparator(';')
			.build()
			.parse();
	}
}
