package hive.apps.home;

import java.util.ArrayList;

public class TimetableWidgetListItems {

	public ArrayList<TimetableItem> ITEMS = new ArrayList<TimetableItem>();
	public ArrayList<String> LESSONS = new ArrayList<String>();


	public TimetableWidgetListItems() {
		addItem(new TimetableItem("English", "1", "Selman Eser", "8:20 - 9:00"));
		addItem(new TimetableItem("Mathematics", "2", "Vehid Kurtic" , "9:15 - 9:55"));
		addItem(new TimetableItem("IT", "3", "Omer Korkmaz", "10:05 - 10:45"));
		addItem(new TimetableItem("IT", "4", "Omer Korkmaz", "10:55 - 11:35"));
		addItem(new TimetableItem("Biology", "5", "Mesut Karatas", "11:45 - 12:25"));
		addItem(new TimetableItem("Bosnian", "6", "Mirela Corbo", "13:10 - 13:50"));
		addItem(new TimetableItem("Chemistry", "7", "Armin Tresnjo", "14:00 - 14:40"));
		addItem(new TimetableItem("Democracy", "8", "Mirhan Kiso", "14:50 - 15:30"));
	}

	public static class TimetableItem {
		public String lesson;
		public String lessonnumber;
		public String teacher;
		public String lessontimes;
		
		public TimetableItem(String lesson, String lessonnumber, String teacher, String lessontimes) {
			this.lesson = lesson;
			this.lessonnumber = lessonnumber;
			this.teacher = teacher;
			this.lessontimes = lessontimes;
			
			

		}
	}

	public void addItem(TimetableItem item) {
		ITEMS.add(item);
	}
}