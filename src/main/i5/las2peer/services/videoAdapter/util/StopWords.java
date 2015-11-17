package i5.las2peer.services.videoAdapter.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StopWords {
	
	public String remove(String searchString){
	
		String stemmedSearchString="";
		int k=0,i,j;
		//ArrayList<String> wordsList = new ArrayList<String>();
		String sCurrentLine;
		String[] stopwords = new String[600];
		try{
	        FileReader fr=new FileReader("/home/siddiqui/services/LAS2peer-Vaptor-Adapter-Service/etc/stopwordslist.txt");
	        BufferedReader br= new BufferedReader(fr);
	        while ((sCurrentLine = br.readLine()) != null){
	            stopwords[k]=sCurrentLine;
	            k++;
	        }
	        String[] words = searchString.split(" ");
	        ArrayList<String> wordsList = new ArrayList<String>();
	        Set<String> stopWordsSet = new HashSet<String>();
	        //stopWordsSet.add("I");
	        //stopWordsSet.add("THIS");
	        //stopWordsSet.add("AND");
	        //stopWordsSet.add("THERE'S");

	        for(String word : words)
	        {
	            String wordCompare = word.toUpperCase();
	            if(!stopWordsSet.contains(wordCompare))
	            {
	                wordsList.add(word);
	            }
	        }
	        for (String str : wordsList){
	        	stemmedSearchString+=str+" ";
	        	
	            System.out.println(str+" ");
	        }   
	    }catch(Exception ex){
	        System.out.println(ex);
	    }
		
		return stemmedSearchString;
	}
	

}
