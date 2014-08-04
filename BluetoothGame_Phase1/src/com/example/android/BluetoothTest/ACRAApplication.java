package com.example.android.BluetoothTest;



import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey=""
,mailTo = "xuanbach0805@gmail.com",
customReportContent = { ReportField.APP_VERSION_CODE, 
					ReportField.APP_VERSION_NAME, 
					ReportField.ANDROID_VERSION, 
					ReportField.PHONE_MODEL, 
					ReportField.CUSTOM_DATA, 
					ReportField.STACK_TRACE, 
					ReportField.LOGCAT },                
mode = ReportingInteractionMode.TOAST
,resToastText = R.string.crash_toast_text
)

public class ACRAApplication extends Application {
	 public ConnectWebSocket connecWebSocket;
	 
	 private static ACRAApplication mInstance;
	
	  @Override
	  public void onCreate() {
	    ACRA.init(this);
//	    ACRA.getErrorReporter().setReportSender(new HockeySender());
	    mInstance = this;
	    
	    connecWebSocket = new ConnectWebSocket();
	    super.onCreate();
	  }
	  
	  public static synchronized ACRAApplication getInstance() {
		  return mInstance;
		 }
	}