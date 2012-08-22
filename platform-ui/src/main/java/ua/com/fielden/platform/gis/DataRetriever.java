package ua.com.fielden.platform.gis;

import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class DataRetriever {

    public static List<PathElement> getData() {
	try {
	    //csv file containing data
	    final String strFile = "src/main/resources/gis/gis-data-sample.csv";

	    //create BufferedReader to read csv file
	    final BufferedReader br = new BufferedReader(new FileReader(strFile));
	    String strLine = "";
	    StringTokenizer st = null;
	    int lineNumber = 0;

	    final List<PathElement> list = new ArrayList<PathElement>();

	    //read comma separated file line by line
	    while ((strLine = br.readLine()) != null) {
		lineNumber++;
		//break comma separated line using ","
		st = new StringTokenizer(strLine, ",");

		if (lineNumber == 1) {
		    st.nextToken();
		    st.nextToken();
		    st.nextToken();
		    st.nextToken();
		    st.nextToken();
		} else {
		    final DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		    final Date d1 = df.parse(st.nextToken());
		    final Date d2 = df.parse(st.nextToken());
		    final int speed = Integer.valueOf(st.nextToken());
		    final double latitude = Double.valueOf(st.nextToken()), longitude = Double.valueOf(st.nextToken());;
		    final PathElement pe = new PathElement(d1, d2, speed, latitude, longitude);
		    list.add(pe);
		}
	    }
	    return list;
	} catch (final Exception e) {
	    System.out.println("Exception while reading csv file: " + e);
	    return null;
	}
    }
}
