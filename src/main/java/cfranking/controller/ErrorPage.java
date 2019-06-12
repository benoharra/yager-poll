package cfranking.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ErrorPage {
	
	private static FileWriter fw_error;
	private static File file_error;
	private static BufferedWriter bw_error;
	
	public static String dirHome;
	public static boolean connor = true;
	
	static{
		if(connor){
			dirHome = "C:\\Users\\Connor Yager\\Documents\\Rankings\\Data\\";
		} else {
			dirHome = "C:\\Users\\ospre\\Rankings\\";
		}
	}
	
	
	public static void writeError(String error){
		try{
			if(file_error == null){
				file_error = new File(dirHome + "Errors.txt");
				file_error.createNewFile();
				fw_error = new FileWriter(file_error.getAbsoluteFile());
				bw_error = new BufferedWriter(fw_error);
			}
			bw_error.write(error);
			bw_error.write(System.getProperty("line.separator"));
		} catch(IOException e){
			e.printStackTrace();
		}
	}
	
	public static void close(){
		if(!bw_error.equals(null)){
			try {
				bw_error.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
