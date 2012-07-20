package br.usp.sdext.util;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Misc {

	private static final Calendar now = Calendar.getInstance();

	public static Date parseDate(String str) {

		try {

			if (str.equals("")) {
				return null;
			}

			Date date = null;

			try {date =  new SimpleDateFormat("dd/MM/yy").parse(str);} 
			catch (Exception e) {}

			if (date == null) {

				try {date =  new SimpleDateFormat("ddMMyyyy").parse(str);} 
				catch (Exception e) {}
			}

			if (date == null) {

				try {date =  new SimpleDateFormat("dd-MMM-yy").parse(str);} 
				catch (Exception e) {return null;}
			}


			Calendar parseDate = Calendar.getInstance();
			parseDate.setTime(date);

			int year = parseDate.get(Calendar.YEAR);

			// 0067 case
			if (year < 100) {

				year = year + 1900;
			}

			// 7971 case
			if (parseDate.after(now)) {

				int first = (int) Math.floor(year / 1000);

				if (first > 2) {
					
					year -= (first - 1) * 1000;
					
				} else {
					
					year -= 100;
				}
			}

			parseDate.set(Calendar.YEAR, year);  

			date = new Date(parseDate.getTime().getTime());

			return date;

		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}

	public static String parseStr(String str) {

		str = parseString(str);

		if (str != null) {
			str =  str.replace("-", " "); 
		}
		return str;
	}

	private static String parseString(String str) {

		try {
			str = Normalizer.normalize(str, Normalizer.Form.NFD);
			str = str.replaceAll("[^\\p{ASCII}]", "");

			Pattern pattern = Pattern.compile("\\s+");
			Matcher matcher = pattern.matcher(str);
			str = matcher.replaceAll(" ");

			// Check for NAO
			pattern = Pattern.compile("^(\\bNAO\\b)$");
			matcher = pattern.matcher(str);
			if (matcher.find()) return null;

			pattern = Pattern.compile("^(\\bNAO\\b) (\\bINFORMAD[A,O]\\b).*$");
			matcher = pattern.matcher(str);
			if (matcher.find()) return null;

			pattern = Pattern.compile("^(\\bNAO\\b) (\\bCONHECIMENTO\\b).*$");
			matcher = pattern.matcher(str);
			if (matcher.find()) return null;

			pattern = Pattern.compile("^(\\bNAO\\b) (\\bCONSTA\\b).*$");
			matcher = pattern.matcher(str);
			if (matcher.find()) return null;

			// Check for SIM
			pattern = Pattern.compile("^\\bSIM\\b$");
			matcher = pattern.matcher(str);
			if (matcher.find()) return null;

			if (str.contains("#")) return null;

			if (str.equals("#NE#") || str.equals("#NI#") || str.equals("")) return null;

			return str.trim().toUpperCase();
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}

	public static Long parseLong(String str) {

		try {
			if ((str = parseString(str)) == null) return null;

			str = str.replace(" ", "");

			Long no;

			try {

				no =  Long.parseLong(str);

				return (no < 0 ) ? null : no;

			} catch (Exception e) {

				str = str.replace("-", "");

				if (str.equals("")) return null;

				no =  Long.parseLong(str);

				return (no < 0 ) ? null : no;
			}
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}

	public static Integer parseInt(String str) {

		try {
			if ((str = parseString(str)) == null) return null;

			str = str.replace(" ", "");

			if (str.equals("")) return null;

			Integer no = Integer.parseInt(str);

			return (no < 0 ) ? null : no;
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}

	public static Float parseFloat(String str) {

		try {
			if ((str = parseString(str)) == null) return null;

			str = str.replace(" ", "");

			str = str.replace(",", ".");

			if (str.equals("")) return null;

			Float no = Float.parseFloat(str); 

			return (no < 0 ) ? null : no;
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}

	public static Integer getAge(Date birthDate) {

		try {
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
		} catch (Exception e) {
			System.err.println(e.getLocalizedMessage());
			return null;
		}
	}

	public static void main(String[] args) {

		String teste1 = "NAO CONSTA";
		String teste2 = "NAO CONSTA NO DOCUMENTO";
		String teste3 = "NAO-ME-TOQUE";

		String teste4 = "NAO NAO";
		String teste5 = "JOAO SIMPSON NAO DA SALIA";
		String teste6 = "NAO JOÃO SIM TO MUITO";

		System.out.println(parseStr(teste1));
		System.out.println(parseStr(teste2));
		System.out.println(parseStr(teste3));

		System.out.println();

		System.out.println(parseStr(teste4));
		System.out.println(parseStr(teste5));
		System.out.println(parseStr(teste6));
	}

}
