package cn.edu.whu.openmi.websocket2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import cn.edu.whu.openmi.floodmodels.GisVolumn.DemData;
import cn.edu.whu.openmi.smw.SimpleWrapper;
import cn.edu.whu.openmi.util.OpenMIUtilities;
import cn.edu.whu.openmi.websocket.WSModelUtils;
import nl.alterra.openmi.sdk.backbone.TimeStamp;
import nl.alterra.openmi.sdk.utilities.CalendarConverter;

@ServerEndpoint(value = "/ws/gisvolumn")
public class WSGisVolumn extends SimpleWrapper{
	
	public final static String Flood_Volumn_Key = "FloodVolumn_Value";
	public final static String Flood_Elev_Key = "FloodElev_Value";

	//public Double[][] dem_value;
	private Session session;
	private String nickName;
	
	private String inputFloodVolumn,outputFloodElev;
	private String out_file = null;
	private static Double[][] dem_value = null;
	//private double[][] dem_array;
	private DemData dem_data;
	
	private Map<Calendar, Double> outputValues = new LinkedHashMap<Calendar, Double>();
	
	
	public WSGisVolumn(){
		InputStream inConfig = null;
		try {
			String path = WSTopModel2.class.getClassLoader().getResource("").getPath()+"cn/edu/whu/openmi/floodmodels/GisVolumn/GisVolumn-config.xml";
			inConfig = new FileInputStream(new File(path));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   	 this.setVariablesFromConfigFile(inConfig);
	}
	
	@OnOpen
	public void onOpen(Session session,
			@PathParam(value = "nickName") String nickName,EndpointConfig config){
		this.session = session;
		this.nickName = nickName;
		this.session.setMaxIdleTimeout(1800000);
		this.session.setMaxTextMessageBufferSize(104857700);
		//HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
		//httpSession.setMaxInactiveInterval(1800000);
		//System.out.println(JSON.toJSONString(httpSession));
		//System.out.println(JSON.toJSONString(session.getRequestParameterMap()));
		//this.session.s
		Map<String, Object> map = config.getUserProperties();
		for(String key : map.keySet()){
			System.out.println(map.get(key));
		}
		/*WebSocketContainer wsContainer = this.session.getContainer();
		wsContainer.setAsyncSendTimeout(2000000);
		wsContainer.setDefaultMaxSessionIdleTimeout(2000000);
		wsContainer.setDefaultMaxTextMessageBufferSize(602500);*/
		
		int textSize = this.session.getMaxTextMessageBufferSize();
		long timeOut = this.session.getMaxIdleTimeout();
		
		System.out.println("---------------");
		System.out.println("textSize = "+textSize);
		System.out.println("timeOut = " + timeOut);
		
		System.out.println("Connection Opened");
		Map<String, String> pathParameters = session.getPathParameters();
		Map<String, List<String>> reqParams = session.getRequestParameterMap();
		HashMap paraMap = new HashMap<>();
		Set set = reqParams.keySet();
		Iterator it = set.iterator();
		int i = 0;
		while (it.hasNext()) {
			String key = (String) it.next();
			String value = ((List<String>) reqParams.get(key)).get(0);
			// arguments[i++] = new Argument(key,value,true);
			paraMap.put(key, value);
		} 	
		this.initialize(paraMap);
	
	}
	
	@OnClose
	public void onClose() {
		// connections.remove(this);
		String message = String.format("System> %s, %s", this.nickName,
				" has disconnection.");
		//this.session.setMaxIdleTimeout(1000000);
		// WebSocketModels.broadCast(message);
		System.out.println(message);
		this.finish();
	}
	
	@OnMessage
	public void onMessage(String message,
			@PathParam(value = "nickName") String nickName){
		
		//System.out.println(message);
		Map<String, String> paraMap = WSModelUtils.parseArguments(message);
		
		System.out.println("hello");
		this.inputFloodVolumn = paraMap.get(Flood_Volumn_Key);
		this.performTimeStep();
		//System.out.println("hello");
		//System.out.println(this.outputFloodElev);
		String resp = WSModelUtils.METHODKEY + "=set&" + Flood_Elev_Key + "=" + this.outputFloodElev;
		try {
			this.session.getBasicRemote().sendText(WSModelUtils.appendToByte(resp, 102400));
			System.out.println(resp);
		} catch (IOException e) {
			// TODO: handle exception 
			e.printStackTrace();
		}
	}
	
	@OnError
	public void onError(Throwable throwable) {
		System.out.println(throwable.getMessage());
	}
	
	@Override
	public void initialize(HashMap properties){
		super.initialize(properties);
		out_file = properties.get("out_file").toString();
		this.dem_data = getDemData("/home/fgao/websocket/openmi/floodmodel1/dem_out.txt");
		System.out.println("initialized successfully");
		setInitialized(true);	
	
	}
	
	@Override
	public boolean performTimeStep() {
		double v_water = Double.parseDouble(this.inputFloodVolumn);
		TimeStamp time = (TimeStamp)this.getCurrentTime();
		Calendar curr_time = CalendarConverter.modifiedJulian2Gregorian(time.getModifiedJulianDay());
        
		Double result = calWaterElevation(v_water, dem_value, dem_data.elevMin, dem_data.elevMax, Math.abs(dem_data.scaleX * dem_data.scaleY));
//		System.out.println("time:"+curr_time.getTime()+"; flood elevation:"+result);
		//setOutputItem(result);
		this.outputFloodElev = result.toString();
		//this.setValues(this.getOutputExchangeItem(0).getQuantity().getID(), this.getOutputExchangeItem(0).getElementSet().getID(), new ScalarSet(new double[]{result}));
		outputValues.put(curr_time, result);
		
		//这里时间可以从客户端传过来，或者使用advanceTime自行递增,即更新上述的this.getCurrentTime()
		this.advanceTime();
		return true;
	}
	
	@Override
	public void finish() {
		try {
			if (OpenMIUtilities.isEmpty(this.out_file)) {
				this.out_file = OpenMIUtilities.getCurrentWorkingDirectory()+File.separator+new Date().getSeconds()+"_GisVolumnOutPut.txt";
			}
			File file = new File(this.out_file);
			if(!file.exists()){
				file.createNewFile();
			}
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			String line ="";
			String timeFormat = "yyyy-MM-dd hh:mm:ss";
			bw.write("The flood elev...");
			bw.newLine();
			Calendar startCalendar =CalendarConverter.modifiedJulian2Gregorian( this.getTimeHorizon().getStart().getModifiedJulianDay());
			 Calendar endCalendar =CalendarConverter.modifiedJulian2Gregorian( this.getTimeHorizon().getEnd().getModifiedJulianDay());
//			 line = "StartDate: "+startCalendar.getTime().toString();
			 line = "StartDate: "+OpenMIUtilities.calendar2Str(startCalendar, timeFormat);
			 bw.write(line);
			 bw.newLine();
//			 line = "EndDate: "+endCalendar.getTime().toString();
			 line = "EndDate: "+OpenMIUtilities.calendar2Str(endCalendar, timeFormat);
			 bw.write(line);
			 bw.newLine();
			 bw.newLine();
			 line = "Time["+timeFormat+"], Flood_Elevation";
			 bw.write(line);
			 bw.newLine();
			 
			 for (Calendar calendar:outputValues.keySet()) {
				String time = OpenMIUtilities.calendar2Str(calendar, timeFormat);
				line = time +","+outputValues.get(calendar);
//				System.out.println(line);
				bw.write(line);
				bw.newLine();
			}
			 bw.flush();
			 bw.newLine();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			this.addError(e.getMessage());
		}
	}
	
	
	
	
	public Double calWaterElevation(Double volumn, Double[][] dem_value, Double elevMin, Double elevMax, Double areaUnit){
		Double eLow = elevMin;
		Double eHigh = elevMax;
		Double eMid = (eLow + eHigh) / 2.0;
		while (eHigh - eLow > 0.0001) {
		      Double vLow = volumn - calcWaterVolumn(eLow, dem_value, areaUnit);
		      Double vMid = volumn - calcWaterVolumn(eMid, dem_value, areaUnit);
		      if (vLow * vMid <= 0.0) {
		        eHigh = eMid;
		      } else {
		        eLow = eMid;
		      }
		      eMid = (eLow + eHigh) / 2.0;
		    }
		
		return eMid;
	}
	
	public Double calcWaterVolumn(Double elev_water, Double[][] dem_value, Double areaUnit){
		Double V = 0.0;
		for (int i = 0; i < dem_value.length; i++) {
			for (int j = 0; j < dem_value[i].length; j++) {
				if (dem_value[i][j]>-9999) {
					Double diff = elev_water - dem_value[i][j];
					if (diff > 0) {
						V += diff*areaUnit;
					}
				}
			}
		}
		return V;
	}
	
	//read from txt
		public DemData getDemData(String dem_path){
			
			try{
				
				InputStream is = new FileInputStream(new File(dem_path));					

				BufferedReader br = new BufferedReader(new InputStreamReader(is));
			    
				String line_info = br.readLine();
				String[] dem_info = line_info.split(";");
				
				DemData dem_data = new DemData();
				dem_data.setElevMax(Double.valueOf(dem_info[0]));
				dem_data.setElevMin(Double.valueOf(dem_info[1]));
				dem_data.setScaleX(Double.valueOf(dem_info[2]));
				dem_data.setScaleY(Double.valueOf(dem_info[3]));
				
			    //int rows = 0;
			    
			    List<List<Double>> numList = new ArrayList<List<Double>>();
			    
			    for (String line = br.readLine();line!=null;line = br.readLine()) {
			    	List<Double> rowList = new ArrayList<>();
			    	String[] temp = line.split(" ");
			    	for (int i = 0; i < temp.length; i++) {
			    		rowList.add(Double.valueOf(temp[i]));
			    	}
			    	numList.add(rowList);
			    	//rows++;	
			    }
			    
			    dem_value = new Double[numList.size()][];
			    for (int i = 0; i < numList.size(); i++) {
					dem_value[i] = new Double[numList.get(i).size()];
				}
			    
			    for (int i = 0; i < numList.size(); i++) {
					for (int j = 0; j < numList.get(i).size(); j++) {
						dem_value[i][j] = numList.get(i).get(j);
						//System.out.println(dem_value[i][j]);
					}
				}
			    
			    return dem_data;
			    
				} catch(IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				} 
		}
	
	public DemData getDemData(){
		
		String dbUrl = "jdbc:postgresql://202.114.118.190:5432/waterlogging";
		String dbAcc = "spark";
		String dbPwd = "spark";
		try {
			Class.forName("org.postgresql.Driver");
		} catch (Exception e) {
			// TODO: handle exception
			System.err.println("Couldn't find driver class:");
			e.printStackTrace();
		}
		
		
		try {
			Connection connInDriver = DriverManager.getConnection(dbUrl,dbAcc,dbPwd);
			Statement stateInDriver = connInDriver.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			/*ResultSet tmp = stateInDriver.executeQuery("SELECT * FROM area INNER JOIN landtype ON area.landtypeid=landtype.ltid WHERE areaid = 3 ");
			tmp.next();
			int cn = tmp.getInt("cn");
			System.out.println(cn);*/
			
			ResultSet dem = stateInDriver.executeQuery("SELECT ST_DumpValues(rast, 1, false) As dem " +
		              "FROM demclip WHERE rid=3");
			dem.next();
			dem_value = (Double[][])dem.getArray("dem").getArray();
	
			ResultSet dem_info = stateInDriver.executeQuery("SELECT (ST_SummaryStats(rast)).min, " +
			        "(ST_SummaryStats(rast)).max, " +
			        "(ST_MetaData(rast)).scalex, " +
			        "(ST_MetaData(rast)).scaley " +
			        "FROM public.dem");
			dem_info.next();
			
			if (!dem_value.equals(null)) {
				
				DemData dem_data = new DemData();
				dem_data.setElevMin(dem_info.getDouble("min"));
				dem_data.setElevMax(dem_info.getDouble("max"));
				dem_data.setScaleX(dem_info.getDouble("scalex"));
				dem_data.setScaleY(dem_info.getDouble("scaley"));
				
				connInDriver.close();
				return dem_data;			
			}else{
				
				connInDriver.close();
				return null;
			}
			/*while(area.next()){
				t_p.put(area.getString("timestamp"), area.getDouble("precipitation"));
				System.out.println(area.getString("timestamp")+"	"+area.getDouble("precipitation"));
			}*/
			//System.out.println(area.getRow());
		
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		
		
		
		
		//gdal.AllRegister();
		/*Dataset hDataset = gdal.Open(input_path,gdalconstConstants.GA_ReadOnly);
		if (hDataset == null)  
        {  
            System.err.println("GDALOpen failed - " + gdal.GetLastErrorNo());  
            System.err.println(gdal.GetLastErrorMsg());  
            //return null;
        } 
		
		int iXsize = hDataset.getRasterXSize();
		int iYsize = hDataset.getRasterYSize();
		Vector metadata = hDataset.GetMetadata_List();	
		for (int i = 0; i < metadata.size(); i++) {
			System.out.println(metadata.get(i));
		}*/
		/*Map<String, Double> dem_info = new HashMap<String , Double>();
		try {
			is = new FileInputStream(new File(input_path));
			
		} catch (FileNotFoundException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		try {
			String line = br.readLine();
			String[] info = line.split(",");
			for (int i = 0; i < info.length; i++) {
				String[] tmp = info[i].split(":");
				dem_info.put(tmp[0], Double.parseDouble(tmp[1]));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
		
		
	}
	
	
}
