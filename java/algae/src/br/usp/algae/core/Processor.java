package br.usp.algae.core;

import static com.googlecode.javacpp.Loader.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

import java.awt.Color;
import java.util.Random;

import com.googlecode.javacv.cpp.opencv_core.*;


public class Processor {

	public static void main (String args[]){
		
		String sourcePath = "/home/fatore/workspace/algae/data/3.JPG";
		String targetPath = "/home/fatore/workspace/algae/data/result.JPG";
		
        IplImage src = cvLoadImage(sourcePath, 0);
        cvThreshold(src, src, 130, 255, CV_THRESH_BINARY);
        
        IplImage dst = cvCreateImage(cvGetSize(src), 8, 3);
        CvMemStorage storage = cvCreateMemStorage(0);
        
        CvSeq contour = new CvSeq();
        CvFont dfont = new CvFont();
        
        float hscale = 2f;
        float vscale = 2f;
        float italicscale = 0.0f;
        int thickness = 5;

        String text = "";
        
        cvInitFont(dfont, CV_FONT_HERSHEY_SIMPLEX, hscale, vscale, italicscale, thickness, CV_AA);
        cvNamedWindow("Source", 0);
        cvShowImage("Source", src);
        
        cvNamedWindow("Components", 0);
        cvFindContours(src, storage, contour, sizeof(CvContour.class), CV_RETR_LIST, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
        
        cvZero( dst );

        int count = 0;
        for( ; contour != null; contour = contour.h_next()) {
        	if (Math.abs(cvContourArea(contour, CV_WHOLE_SEQ, 1)) > 50) {
        		count++;
	        	cvDrawContours(dst, contour, CV_RGB(0,0,255), CV_RGB(0,0,255), -1, 10, 8, cvPoint(0, 0));
	        	
//	        	text = "Area: " + cvContourArea(contour, CV_WHOLE_SEQ, 1);
//	        	cvPutText(dst, text, cvPoint(15, 15), dfont, CV_RGB(0, 255, 0));
//	        	System.out.println(text);
//	        	
//	        	text = "ArcLenght: " + cvArcLength(contour, CV_WHOLE_SEQ, -1);
//	        	cvPutText(dst, text, cvPoint(15, 30), dfont, CV_RGB(0, 255, 0));
//	        	System.out.println(text);
	        	
//	        	cvShowImage("Components", dst);
//	        	cvSaveImage(targetPath, dst);
//	        	cvWaitKey(0);
	
//	        	cvZero(dst);
        	}
        }
        
    	text = "Counting: " + count;
    	cvPutText(dst, text, cvPoint(15, 55), dfont, CV_RGB(0, 255, 0));
    	System.out.println(text);
        
        cvSaveImage(targetPath, dst);
        
        
//        IplImage grayImage = cvCreateImage(cvGetSize(image), IPL_DEPTH_8U, 1);
//        cvCvtColor(image, grayImage, CV_BGR2GRAY);
//
//        CvMemStorage mem;
//        CvSeq contours = new CvSeq();
//        CvSeq ptr = new CvSeq();
//        cvThreshold(grayImage, grayImage, 150, 255, CV_THRESH_BINARY);
//        mem = cvCreateMemStorage(0);
//
//        cvFindContours(grayImage, mem, contours, sizeof(CvContour.class) , CV_RETR_CCOMP, CV_CHAIN_APPROX_SIMPLE, cvPoint(0,0));
//
//        Random rand = new Random();
//        for (ptr = contours; ptr != null; ptr = ptr.h_next()) {
//            Color randomColor = new Color(rand.nextFloat(), rand.nextFloat(), rand.nextFloat());
//            CvScalar color = CV_RGB( randomColor.getRed(), randomColor.getGreen(), randomColor.getBlue());
//            cvDrawContours(image, ptr, color, CV_RGB(0,0,0), -1, CV_FILLED, 8, cvPoint(0,0));
//        }
//        cvSaveImage(targetPath, image);
    }
}














