package nl.avans.data;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public interface TestDataProvider {	
	public <T> List<T> read(String name, Class<T> clazz) throws Exception;
}
