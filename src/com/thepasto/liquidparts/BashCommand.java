package com.thepasto.liquidparts;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;



public abstract class BashCommand {
	
	public static String doCmds(String type,String command) {
		String line;
		String retline="";
		OutputStreamWriter osw = null;
		 try {
		       
		       Process p = Runtime.getRuntime().exec(type);
		       
		       osw=new OutputStreamWriter(p.getOutputStream());
		       osw.write(command+"\n");
		       osw.flush();
		       osw.close();
		       p.waitFor();
		       BufferedReader in = new BufferedReader(
		               new InputStreamReader(p.getInputStream()) );
		       while ((line = in.readLine()) != null) {
		    	   retline+=line;
		         System.out.println(line);
		       }
		       in.close();
		     }
		     catch (Exception e) {
		       e.printStackTrace();
		     }
		 return retline;
	}
}