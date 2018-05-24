package core;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class JoinCsv {

	public void JoinCsvFile() throws IOException {
		// TODO Auto-generated method stub
		File file = new File("E:\\\\PoonamAris\\\\ArisJava\\\\bin\\\\final.csv");
		BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsolutePath()));
	    StringBuilder sb = new StringBuilder();
	    if(!file.exists()){
            file.createNewFile();
      }	
	    BufferedReader br1=new BufferedReader(new InputStreamReader(new FileInputStream("E:\\\\\\\\PoonamAris\\\\\\\\ArisJava\\\\\\\\bin\\\\\\\\NodeRelationCsv.csv")));
	    BufferedReader br2=new BufferedReader(new InputStreamReader(new FileInputStream("E:\\\\PoonamAris\\\\ArisJava\\\\bin\\\\relation.csv")));
	    String line1,line2;     
        
	    while((line1=br1.readLine())!=null && (line2=br2.readLine())!=null){
	        String line=line1+","+line2;
	      //  System.out.println("line== "+line);	         
     
	        sb.append(line);    			
    		sb.append('\n');

	        //write this line to the output file
	    }
		bw.write(sb.toString());

	    bw.flush();
        bw.close();

	}

}
