package com.example.demo;

public class Employee {
	
	private String name;
	private String phone;
	private String country;
	private String email;
	private String address;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getCountry() {
		return country;
	}
	public Employee(String name, String phone, String country, String email, String address) {
		super();
		this.name = name;
		this.phone = phone;
		this.country = country;
		this.email = email;
		this.address = address;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public Employee() {
	
	}
	public void setAddress(String address) {
		this.address = address;
	}

}
