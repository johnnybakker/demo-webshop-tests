package nl.avans.data;

import java.util.List;

public interface TestDataProvider {	
	public <T> List<T> read(String name, Class<T> clazz) throws Exception;
}
