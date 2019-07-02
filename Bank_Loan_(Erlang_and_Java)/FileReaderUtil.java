
/*
 * @author : Himanshu Kohli
 * @version : 1.0
 * @date 23 June 2019
 * Utility class 
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FileReaderUtil {
	
	/*
	 * This method reads the file and returns an arrayList of the elements
	 */
	public ArrayList<DataHolder> readFile(String fileName,String type) {
		
		ArrayList<DataHolder> list = new ArrayList<>();
		try {
			File file = new File(fileName);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String st;
			while((st = reader.readLine()) != null) {
				String name[] = parse(st).split(",");
				if(type.equalsIgnoreCase("BANK"))
					list.add(new DataHolder(name[0],Integer.parseInt(name[1]),Integer.parseInt(name[1])));
				else
					list.add(new DataHolder(name[0],Integer.parseInt(name[1])));
			}
			reader.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public String parse(String element) {	
		return(element.substring(1, element.length()-2));
	}
}
