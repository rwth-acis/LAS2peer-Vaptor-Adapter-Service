package i5.las2peer.services.videoAdapter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.TimeUnit;

import com.xuggle.mediatool.IMediaReader;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.MediaToolAdapter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.mediatool.event.IVideoPictureEvent;

public class VideoSplitter {
	
	private File videoFile=null;
	private int timeIntervalInMin=1;

//	private static final TimeUnit TIME_UNIT = TimeUnit.MICROSECONDS;	
	
	public VideoSplitter(File video, int timeInterval) {
		System.out.println("hello_advanced_constructor");
		if(video==null || !video.exists() || video.isDirectory()) {
			throw new RuntimeException("The video file is not valid:"+video);
		}
		videoFile=video;
		if(timeInterval>=5) {
			timeIntervalInMin=timeInterval;
		}
	}
	
	public void splitFiles(double startTimeInMin, double stopTimeInMin) throws Exception {
		System.out.println("hello_advanced");
		//long inputTimeIntervalInMillies=TimeUnit.MICROSECONDS.convert(timeIntervalInMin, TimeUnit.MINUTES);
		//long startTimeInMillies=TimeUnit.MICROSECONDS.convert(startTimeInMin, TimeUnit.MINUTES);
		//long stopTimeInMillies=TimeUnit.MICROSECONDS.convert(stopTimeInMin, TimeUnit.MINUTES);
		
		double startTimeInMillies=startTimeInMin*60000000;
		double stopTimeInMillies=stopTimeInMin*60000000;
		
		//create a media reader
	  	IMediaReader mediaReader = ToolFactory.makeReader(videoFile.getAbsolutePath());
	  	 // have the reader create a buffered image that others can reuse
	  	mediaReader.setBufferedImageTypeToGenerate(BufferedImage.TYPE_3BYTE_BGR);
	  	Cutter cutter=new Cutter();
	  	
		//add a viewer to the reader, to see the decoded media
		mediaReader.addListener(cutter);
		//read and decode packets from the source file and
		//dispatch decoded audio and video to the writer
		//int fileCounter=1;
		String url=videoFile.getAbsolutePath().substring(0, videoFile.getAbsolutePath().lastIndexOf("."))+"_"+"trimmed"+".mp4";
		IMediaWriter writer=ToolFactory.makeWriter(url,mediaReader);
		//cutter.addListener(writer);
		int flag=0;
		while (mediaReader.readPacket()==null) {
			System.out.println("cutter.getTimeCounter()="+cutter.getTimeCounter());			
			//if(cutter.getTimeCounter()>inputTimeIntervalInMillies) {
			if(cutter.getTimeCounter()>startTimeInMillies && flag==0) {
				flag=1;
				System.out.println("startTimeIntervalInMillies="+startTimeInMillies);
				cutter.addListener(writer);
				//cutter.removeListener(writer);
				//writer.close();// flusing and closing earlier writers..
				//fileCounter++;
				//url=videoFile.getAbsolutePath().substring(0, videoFile.getAbsolutePath().lastIndexOf("."))+"_"+fileCounter+".mp4";
				//writer=ToolFactory.makeWriter(url,mediaReader);
				//writer.addListener(ToolFactory.makeDebugListener());
				//inputTimeIntervalInMillies=inputTimeIntervalInMillies+TimeUnit.MICROSECONDS.convert(timeIntervalInMin, TimeUnit.MINUTES); // next time slot..
				//cutter.addListener(writer);
			}
			if(cutter.getTimeCounter()>stopTimeInMillies && flag==1) {
				System.out.println("stopTimeIntervalInMillies="+stopTimeInMillies);
				cutter.removeListener(writer);
				writer.close();// flusing and closing earlier writers..
				//fileCounter++;
				url=videoFile.getAbsolutePath().substring(0, videoFile.getAbsolutePath().lastIndexOf("."))+"_"+"trimmed"+".mp4";
				writer=ToolFactory.makeWriter(url,mediaReader);
				writer.addListener(ToolFactory.makeDebugListener());
				//inputTimeIntervalInMillies=inputTimeIntervalInMillies+TimeUnit.MICROSECONDS.convert(timeIntervalInMin, TimeUnit.MINUTES); // next time slot..
				//cutter.addListener(writer);
				break;
			}
		}
		System.out.println("startTimeInMin="+startTimeInMin);
		System.out.println("startTimeIntervalInMillies="+startTimeInMillies);
		System.out.println("stopTimeInInMin="+stopTimeInMin);
		System.out.println("stopTimeIntervalInMillies="+stopTimeInMillies);
		
		//writer.close();// flusing and closing earlier writers..
		mediaReader.close();
	}

	public class Cutter extends MediaToolAdapter { 

		private long timeCounterInMillies=0;
	
		@Override
		public void onVideoPicture(IVideoPictureEvent arg0) {
			timeCounterInMillies=arg0.getTimeStamp();
			super.onVideoPicture(arg0);
		}
		
		public long getTimeCounter() {
			return timeCounterInMillies;
		}
	}
}