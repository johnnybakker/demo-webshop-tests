package nl.avans.data;

import com.opencsv.bean.CsvBindByName;

public class User  {

	public User() {
		
	}

	@CsvBindByName(column = "email")
	public String email;

	@CsvBindByName(column = "password")
	public String password;
}
