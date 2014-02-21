package org.zero.app.spring.mvc.dto;

public class TestDto {
	private int numberVal;
	private String strVal;
	private String strVal2;
	public TestDto()
	{
		
	}
	public TestDto(int numberVal, String strVal, String strVal2) {
		super();
		this.numberVal = numberVal;
		this.strVal = strVal;
		this.strVal2 = strVal2;
	}
	public int getNumberVal() {
		return numberVal;
	}
	public void setNumberVal(int numberVal) {
		this.numberVal = numberVal;
	}
	public String getStrVal() {
		return strVal;
	}
	public void setStrVal(String strVal) {
		this.strVal = strVal;
	}
	public String getStrVal2() {
		return strVal2;
	}
	public void setStrVal2(String strVal2) {
		this.strVal2 = strVal2;
	}
	@Override
	public String toString() {
		return "TestDto [numberVal=" + numberVal + ", strVal=" + strVal
				+ ", strVal2=" + strVal2 + "]";
	}
}
