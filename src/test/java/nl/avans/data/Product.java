package nl.avans.data;

import com.opencsv.bean.CsvBindByName;

// Single line of javascript to aquire products from demo webshop page
// console.log(Array.from(document.querySelectorAll("[data-productid]")).map(e => `${e.getAttribute("data-productid")};${e.querySelector(".product-title a").innerText};${location.pathname.substring(1)};${(e.querySelector(".buttons").children.length > 0)};${(e.querySelector(".actual-price").innerText)}`).join("\n"))

public class Product {

	@CsvBindByName(column = "id")
	protected int id;

	@CsvBindByName(column = "name")
	protected String name;

	@CsvBindByName(column = "category")
	protected String category;

	@CsvBindByName(column = "subcategory")
	protected String subcategory;

	@CsvBindByName(column = "available")
	protected boolean available;

	@CsvBindByName(column = "price")
	protected String price;

	public Product() {

	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getCategory() {
		return category;
	}

	public String getSubCategory() {
		return subcategory;
	}

	public boolean isAvailable() {
		return available;
	}

	public double getPrice() {
		return Double.parseDouble(price);
	}
}