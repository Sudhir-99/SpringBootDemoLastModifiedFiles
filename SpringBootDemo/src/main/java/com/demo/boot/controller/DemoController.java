package com.demo.boot.controller;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/apis")
public class DemoController {
	
	static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	static Map<Date, String> mapTree = new HashMap<Date, String>();
	
	
	// http://localhost:8080/apis/getLastAccessedFiles
	@RequestMapping(value = "/getLastAccessedFiles",method = RequestMethod.GET, produces={"application/json"})
	public String getLastAccessedFiles() throws Exception {
	    try {
	    	JSONObject jobj = new JSONObject();
			File currentDir = new File(".");        // current directory  
			displayDirectoryContents(currentDir);   
	    	int count = 0;
			int max=5;
			Map<Date, String> m1 = new TreeMap<Date, String>(mapTree).descendingMap();   // sorting map according to descending order
		   
	        Field changeMap = jobj.getClass().getDeclaredField("map");   // for sorting JSON Object
	        changeMap.setAccessible(true);
	        changeMap.set(jobj, new LinkedHashMap<>());
	        changeMap.setAccessible(false);
	        for (Map.Entry<Date, String> entry : m1.entrySet()){
		    	if (count >= max) break;
		    	System.out.println("Data:::"+entry.getValue()+"------"+dateFormat.format(entry.getKey()));
		        jobj.put(entry.getValue(), dateFormat.format(entry.getKey()));     // put data into JSON Object
		        count++;
		    }
	        return jobj.toString();
	      } catch (IllegalAccessException | NoSuchFieldException e) {
	    	 System.out.println(e.getMessage());
	    	 return e.getMessage();
	      }
	}

	public static void displayDirectoryContents(File dir) throws IOException, ParseException {
		File[] files = dir.listFiles();
		Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);   // sort files array according to last modified date.
		for (File file : files) {
			if (file.isDirectory()) {
				displayDirectoryContents(file);  // if it is directory then get files from directory recursively.
			}else{
				String fileExt =  getFileExtension(file);
				if(fileExt.isEmpty()) {
				}else{
				   if(file.getName().equals("SpringBootDemoApplication.class")) {
				   }else{
				     String strDate = dateFormat.format(new Date(file.lastModified()));
					 mapTree.put( dateFormat.parse(strDate),file.getName());      // put filename and last access date into map.
				   }
				}
			  }
		 }
	}
	
  // getFileExtension method return file extension
	private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
        return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

}
