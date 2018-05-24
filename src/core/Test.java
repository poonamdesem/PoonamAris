package core;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Test {

    public static void main(String args[]) throws Exception{
        Test csvWriter = new Test(new File("E:\\\\PoonamAris\\\\ArisJava\\\\bin\\\\test1.csv"), new File("E:\\\\PoonamAris\\\\ArisJava\\\\bin\\\\test2.csv"), 16);
        csvWriter.writeNextCol(Arrays.asList(new String[]{"A", "AA", "AAA","AAAA"}));
        csvWriter.writeNextCol(Arrays.asList(new String[]{"B", "BB", "CC","DD"}));
        csvWriter.writeNextCol(Arrays.asList(new String[]{"third0", "third1", "third2","third2"}));
        csvWriter.writeNextCol(Arrays.asList(new String[]{"second0", "second1", "second3","second4"}));
        //ArrayList<String> cols =readFile("E:\\\\PoonamAris\\\\ArisJava\\\\bin\\\\NodeRelationCsv.csv");
     /*   ArrayList<String> cols =readFile("E:\\\\PoonamAris\\\\ArisJava\\\\bin\\\\finalWrite1.csv");
        Arrays.toString(cols.toArray());
        String colsArr[] = new String[cols.size()];
        int i=0;
        for(String elem : cols){
        	colsArr[i] =elem;
           // System.out.println("final write== "+elem);
            i++;
        }
        ArrayList<String> cols1 =readFile("E:\\\\PoonamAris\\\\ArisJava\\\\bin\\\\finalWrite.csv");
        Arrays.toString(cols1.toArray());
        String colsArr1[] = new String[cols1.size()];
        int j=0;
        for(String elem1 : cols1){
        	colsArr1[j] = elem1;
           // System.out.println("final write== "+Arrays.asList(elem1.split(",")));
            j++;
        }
        String colsArr2[] = new String[cols1.size()];
        String colsArr3 = new String();
        String colsArr4= new String();

        String colsArr5 = new String();

        for (int k = 0; k < colsArr1.length; k++) { 
        	colsArr2 =colsArr1[k].split(",");
            System.out.println("array ="+colsArr2[0]+colsArr2[1]+colsArr2[2]); 
             System.out.println(Arrays.asList(colsArr1[k].split(","))); 
             colsArr3 = colsArr2[0];
             colsArr4 = colsArr2[1];
             colsArr5 = colsArr2[2];           
             
         } */
        
       // csvWriter.writeNextCol(Arrays.asList(colsArr));
       // csvWriter.writeNextCol(Arrays.asList(colsArr3));
       // csvWriter.writeNextCol(Arrays.asList(colsArr4));
        //csvWriter.writeNextCol(Arrays.asList(colsArr5));

       //csvWriter.writeNextCol(Arrays.asList(colsArr1));

    }
    private static ArrayList<String> readFile(String fileName) throws IOException
    {
    	ArrayList<String> values = new ArrayList<String>();
       BufferedReader br = new BufferedReader(new  FileReader(fileName));
       
	   String line = null; 
	   int i = 0;
	   while ((line = br.readLine()) != null) {
	    	values.add(line);
		//	System.out.println("linesep==="+line);
	        i++;
	    }
       
	    br.close();
	    return values;
    }
    public void writeNextCol(List<String> colOfValues) throws IOException{
        // we are going to create a new target file so we have to first 
        // create a duplicated version
        copyFile(targetFile, workFile);

        this.targetStream = new BufferedOutputStream(new FileOutputStream(targetFile));

        int lineNo = 0;

        for(String nextColValue: colOfValues){

            String nextChunk = nextColValue + ",";

            // before we add the next chunk to the current line, 
            // we must retrieve the line from the duplicated file based on its the ofset and length 
            System.out.println("lineInBytes=="+lineNo);

            int lineOfset = findLineOfset(lineNo);  

            workRndAccFile.seek(lineOfset);

            int bytesToRead = lineInBytes[lineNo];
            byte[] curLineBytes = new byte[bytesToRead];
            workRndAccFile.read(curLineBytes);

            // now, we write the previous version of the line fetched from the
            // duplicated file plus the new chunk plus a 'new line' character
            targetStream.write(curLineBytes);
            targetStream.write(nextChunk.getBytes());
            targetStream.write("\n".getBytes());

            // update the length of the line
            lineInBytes[lineNo] += nextChunk.getBytes().length; 

            lineNo++;
        }

        // Though I have not done it myself but obviously some code should be added here to care for the cases where 
        // less column values have been provided in this method than the total number of lines

        targetStream.flush();
        workFile.delete();

        firstColWritten = true;
    }

    // finds the byte ofset of the given line in the duplicated file
    private int findLineOfset(int lineNo) {  
        int ofset = 0;
       // System.out.println(lineNo);
        for(int i = 0; i < lineNo; i++)

            ofset += lineInBytes[lineNo] + 
                (firstColWritten? 1:0); // 1 byte is added for '\n' if at least one column has been written
        return ofset;
    }

    // helper method for file copy operation
    public static void copyFile( File from, File to ) throws IOException {
            FileChannel in = new FileInputStream( from ).getChannel();
            FileChannel out = new FileOutputStream( to ).getChannel();
            out.transferFrom( in, 0, in.size() );
    }

    public Test(File targetFile, File workFile, int lines) throws Exception{
        this.targetFile = targetFile;
        this.workFile = workFile;

        workFile.createNewFile();

        this.workRndAccFile = new RandomAccessFile(workFile, "rw");

        lineInBytes = new int[lines];
        for(int i = 0; i < lines; i++)
            lineInBytes[i] = 0;

        firstColWritten = false;
    }

    private File targetFile;
    private File workFile;

    private int[] lineInBytes;
    private OutputStream targetStream;
    private RandomAccessFile workRndAccFile;
    private boolean firstColWritten;

}