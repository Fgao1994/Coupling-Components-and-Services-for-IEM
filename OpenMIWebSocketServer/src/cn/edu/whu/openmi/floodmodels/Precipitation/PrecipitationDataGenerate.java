package cn.edu.whu.openmi.floodmodels.Precipitation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;




public class PrecipitationDataGenerate {
	
	public static void main(String[] args){	
		
		
		String dateFormat = "yyyy-MM-dd HH:mm:ss";
		int min = 11;  
		int dt = 27;
		Date date = new Date();
		
		String line = "";
		BufferedWriter bw = null;
		String[] values = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		/*try {
			date=sdf.parse("2017-04-27 11:11:42");
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//String data = date.toString();
		String data = sdf.format(date);
		date.setMinutes(13);
		data = sdf.format(date);
		//date.
		System.out.println(data);*/
		try {
			 bw = new BufferedWriter(new FileWriter(new File("D:\\websocket\\flood\\PrecipitationInput-all-2.txt")));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		InputStream is = null;
		try{
			is = new FileInputStream(new File("D:\\websocket\\flood\\PrecipitationInput-2.txt"));
		}catch(FileNotFoundException e1){
			e1.printStackTrace();
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try{
			
			for (line = br.readLine();line!=null;line = br.readLine()) {
				
				if(line.startsWith("//")){
					bw.write(line);
					bw.newLine();
					continue;
				}
				bw.write(line);
				bw.newLine();
				values = line.split("\t");
			}
			
			//line = values[0]+"\t"+values[1];
			//System.out.println(values[0]);
			int i ;
		
			try {
				ArrayList<String> date_s = new ArrayList<>();
				ArrayList<Double> value_s = new ArrayList<>();
				//List<Double> value_s = null;
				date = sdf.parse(values[0]);
				int j = date.getHours();
				int j2 = date.getMinutes();

				/*i = 27;
				for ( ; j < 14; j++) {						
					for ( ; j2 < 60; j2++) {
						date.setDate(i);
						date.setHours(j);
						date.setMinutes(j2);
						//System.out.println(sdf.format(date));
						date_s.add(sdf.format(date));
					}
					j2 = 00;
				}*/
				for ( i = 27; i < 28; i++) {
					for ( ; j < 24; j++) {						
						for ( ; j2 < 60; j2++) {
							date.setDate(i);
							date.setHours(j);
							date.setMinutes(j2);
							//System.out.println(sdf.format(date));
							date_s.add(sdf.format(date));
						}
						j2 = 00;
					}
					j = 00;
				}
				
				double s1 = Double.parseDouble(values[1]);
				for (int k = 0; k < date_s.size(); k++) {
					int _max = 90;
					int _min = 85;
					Random random = new Random();

					double s = random.nextInt(_max) % (_max - _min + 1) + _min;
					
				  
					s1 += s/100;
				   /* if (s1 > 190) {
						s1 += s/100000;
					}else {
						s1 += s/100;
					}*/
					//System.out.println(values[1]);
					
					value_s.add(s1);
					bw.write(date_s.get(k)+"\t"+value_s.get(k));
					bw.newLine();
					System.out.println(date_s.get(k)+"\t"+value_s.get(k));
				}
				System.out.println(date_s.size());
				
				
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch(IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try{
				
				br.close();
				bw.flush();
				bw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
		/*int max = 90;
		int min = 85;
		Random random = new Random();

		double s = random.nextInt(max) % (max - min + 1) + min;
		double s1 = s/100;
		System.out.println(s1);
*/
	}

}
