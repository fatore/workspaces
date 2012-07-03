package br.usp.sdext.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {
	
	private static final Calendar now = Calendar.getInstance();
	
	public static Date parseDate(String str) throws Exception {
		
		if (str.equals("")) {
			return null;
		}
		try {
			Date date =  new SimpleDateFormat("dd/MM/yy").parse(str);
			
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			
			int year = cal.get(Calendar.YEAR);
			
			if (cal.after(now)) {
				  throw new Exception("Can't be born in the future");
			}
			
			if (year < 100) {
				
				year = year + 1900;
			}
			
			cal.set(Calendar.YEAR, year);  
			
			date = new Date(cal.getTime().getTime());
			
			return date;
			
		} catch (IllegalArgumentException | ParseException e) {
			try {
				Date date =  new SimpleDateFormat("ddMMyyyy").parse(str);
				
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);
				
				int year = cal.get(Calendar.YEAR);
				
				if (cal.after(now)) {
					throw new Exception("Can't be born in the future");
				}
				
				if (year < 100) {
					
					year = year + 1900;
				}
				
				cal.set(Calendar.YEAR, year);  
				
				date = new Date(cal.getTime().getTime());
				
				return date;
				
			} catch (IllegalArgumentException | ParseException e2) {
				try {
					
					Date date =  new SimpleDateFormat("dd-MMM-yy").parse(str);
					
					Calendar cal = Calendar.getInstance();
					cal.setTime(date);
					
					int year = cal.get(Calendar.YEAR);
					
					if (cal.after(now)) {
						year -= 100;
					}
					
					if (year < 100) {
						
						year = year + 1900;
					}
					
					cal.set(Calendar.YEAR, year);  
					
					date = new Date(cal.getTime().getTime());
					
					return date;
					
				} catch (Exception e3){
					
					throw new Exception();
				}
			}
		}
	}
	
	public static String parseStr(String str) {
		
		Pattern pattern = Pattern.compile("\\s+");
		
		Matcher matcher = pattern.matcher(str);
		
		str = matcher.replaceAll(" ");
		
		pattern = Pattern.compile("\\bNÃO\\b");
		
		matcher = pattern.matcher(str);
		
		if (matcher.find()) return null;
		
		if (str.contains("#")) return null;
				
		if (str.equals("#NE#") || str.equals("#NI#") || str.equals("")) return null;
		
		return str.trim().toUpperCase();
	}
	
	public static Long parseLong(String str) {
		
		if ((str = parseStr(str)) == null) return null;
		
		str = str.replace(" ", "");
		
		Long no;
		
		try {
			
			no =  Long.parseLong(str);
			
			return (no <= 0 ) ? null : no;
			
		} catch (Exception e) {
		
			str = str.replace("-", "");
			
			if (str.equals("")) return null;
			
			no =  Long.parseLong(str);
			
			return (no <= 0 ) ? null : no;
		}
	}
	
	public static Integer parseInt(String str) {
		
		if ((str = parseStr(str)) == null) return null;
		
		str = str.replace(" ", "");
		
		if (str.equals("")) return null;
		
		Integer no = Integer.parseInt(str);
		
		return (no <= 0 ) ? null : no;
	}
	
	public static Float parseFloat(String str) {
		
		if ((str = parseStr(str)) == null) return null;
		
		str = str.replace(" ", "");
		
		str = str.replace(",", ".");
		
		if (str.equals("")) return null;
		
		Float no = Float.parseFloat(str); 
		
		return (no <= 0 ) ? null : no;
	}
	
	public static Integer getAge(Date birthDate) {
		
		Calendar dob = Calendar.getInstance();
		dob.setTime(birthDate);
		
		if (dob.after(now)) {
		  System.err.println("Can't be born in the future");
		  return null;
		}
		
		int year1 = now.get(Calendar.YEAR);
		int year2 = dob.get(Calendar.YEAR);
		
		int age = year1 - year2;
		
		int month1 = now.get(Calendar.MONTH);
		int month2 = dob.get(Calendar.MONTH);
		
		if (month2 > month1) {
			
		  age--;
		} 
		else if (month1 == month2) {
			
		  int day1 = now.get(Calendar.DAY_OF_MONTH);
		  int day2 = dob.get(Calendar.DAY_OF_MONTH);
		  
		  if (day2 > day1) {
			  
		    age--;
		  }
		}

		return age;
	}
}
