package gash.socket.common;

import gash.socket.common.MessageBuilder.MessageType;
import gash.socket.data.Message;

import java.util.ArrayList;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JsonBuilderTest {
	final static String[] sList = { "one", "two", "three", "four" };

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testMapEncoding() throws Exception {
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("name", "bubba");
		data.put("color", "green");
		data.put("food", "noodles");
		data.put("luckyNumber", 10);

		String out = JsonBuilder.encode(data);
		Assert.assertNotNull(out);
		System.out.println("JSON: " + out);
	}

	@Test
	public void testObjectEncoding() throws Exception {
		TestData data = new TestData();
		data.setName("bubba");
		data.setColor("green");
		data.setFood("noodles");
		data.setLuckyNumber(10);

		for (String i : sList)
			data.addItem(i);

		String out = JsonBuilder.encode(data);
		Assert.assertNotNull(out);
		System.out.println("JSON: " + out);

		TestData data2 = JsonBuilder.decode(out, TestData.class);
		Assert.assertNotNull(data2);
		System.out.println("decoded: " + data2.toString());
	}
	
	@Test
	public void testMessageEncoding() throws Exception {
		Message data = new Message();
		data.setSource("test-src");
		data.setType(MessageType.join);
		data.setPayload("Test message");
		
		String out = JsonBuilder.encode(data);
		Assert.assertNotNull(out);
		System.out.println("JSON: " + out);

		Message data2 = JsonBuilder.decode(out, Message.class);
		Assert.assertNotNull(data2);
		System.out.println("decoded: " + data2.toString());
	}

	public static class TestData {
		private String name;
		private String color;
		private String food;
		private int luckyNumber;
		private ArrayList<String> items;

		public String toString() {
			return ("name = " + name + ", color = " + color + ", food = " + food);
		}

		public void addItem(String item) {
			if (items == null)
				items = new ArrayList<String>();

			if (item != null)
				items.add(item);
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getColor() {
			return color;
		}

		public void setColor(String color) {
			this.color = color;
		}

		public String getFood() {
			return food;
		}

		public void setFood(String food) {
			this.food = food;
		}

		public ArrayList<String> getItems() {
			return items;
		}

		public void setItems(ArrayList<String> items) {
			this.items = items;
		}

		public int getLuckyNumber() {
			return luckyNumber;
		}

		public void setLuckyNumber(int luckyNumber) {
			this.luckyNumber = luckyNumber;
		}

	}
}
