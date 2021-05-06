package com.orion.webservice;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import androidx.recyclerview.widget.ItemTouchHelper;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AsyncTaskHelperForGetData extends AsyncTask<Void, Void, String> {

	private IAsyncResponse callback;
	private String WSDL_TARGET_NAMESPACE;
	private String SOAP_ADDRESS;
	private String OPERATION_NAME;
	private String SOAP_ACTION;
	private String OPERATION_PARAMETER_NAME;
	private String OPERATION_PARAMETER_VALUE;
	private String OPERATION_PARAMETER_VALUE_1;
	private String OPERATION_PARAMETER_VALUE_2;
	private String OPERATION_PARAMETER_VALUE_3;
	private List<PropertyInfo> wsProperty = new ArrayList<PropertyInfo>();
	private ProgressDialog aProgressDialog;
	private Context callerContext;
	ItemTouchHelper.ViewDropHandler handler;

	SoapObject request;
	SoapSerializationEnvelope envelope;
	Object response;

	public AsyncTaskHelperForGetData(Caller caller, String WebServiceLink, String WebMethodName,
									 String WebMethodParameterName, String WebMethodParameterValue,
									 List<PropertyInfo> localProperty) {
		callerContext = caller.context;

		this.WSDL_TARGET_NAMESPACE = WebServiceConstants.WSDL_TARGET_NAMESPACE;
		this.SOAP_ADDRESS = WebServiceLink;
		this.callback = caller;
		this.OPERATION_NAME = WebMethodName;
		this.SOAP_ACTION = WSDL_TARGET_NAMESPACE + WebMethodName;
		this.OPERATION_PARAMETER_NAME = WebMethodParameterName;
		this.OPERATION_PARAMETER_VALUE = WebMethodParameterValue;
		this.request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
		this.envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		this.response = null;
		this.wsProperty = localProperty;
	}

	public AsyncTaskHelperForGetData(Caller caller, String WebServiceLink, String WebMethodName,
									  String WebMethodParameterName, String WebMethodParameterValue1,
									  String WebMethodParameterValue2, String WebMethodParameterValue3,
									  List<PropertyInfo> localProperty) {

		callerContext = caller.context;

		this.WSDL_TARGET_NAMESPACE = WebServiceConstants.WSDL_TARGET_NAMESPACE;
		this.SOAP_ADDRESS = WebServiceLink;
		this.callback = caller;
		this.OPERATION_NAME = WebMethodName;
		this.SOAP_ACTION = WSDL_TARGET_NAMESPACE + WebMethodName;
		this.OPERATION_PARAMETER_NAME = WebMethodParameterName;
		this.OPERATION_PARAMETER_VALUE_1 = WebMethodParameterValue1;
		this.OPERATION_PARAMETER_VALUE_2 = WebMethodParameterValue2;
		this.OPERATION_PARAMETER_VALUE_3 = WebMethodParameterValue3;

		this.request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
		this.envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		this.response = null;
		this.wsProperty = localProperty;
	}

	public AsyncTaskHelperForGetData(Caller caller, String WebServiceLink, String WebMethodName,
									 String WebMethodParameterName, String WebMethodParameterValue1,
									 String WebMethodParameterValue2,
									 List<PropertyInfo> localProperty) {
		callerContext = caller.context;

		this.WSDL_TARGET_NAMESPACE = WebServiceConstants.WSDL_TARGET_NAMESPACE;
		this.SOAP_ADDRESS = WebServiceLink;
		this.callback = caller;
		this.OPERATION_NAME = WebMethodName;
		this.SOAP_ACTION = WSDL_TARGET_NAMESPACE + WebMethodName;
		this.OPERATION_PARAMETER_NAME = WebMethodParameterName;
		this.OPERATION_PARAMETER_VALUE_1 = WebMethodParameterValue1;
		this.OPERATION_PARAMETER_VALUE_2 = WebMethodParameterValue2;

		this.request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);
		this.envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		this.response = null;
		this.wsProperty = localProperty;
	}

		protected void onPreExecute() {
		super.onPreExecute();

		aProgressDialog = new ProgressDialog(callerContext);
		aProgressDialog.setMessage("Processing. Please Wait...");
		aProgressDialog.setCanceledOnTouchOutside(false);
		aProgressDialog.show();
	}

	@Override
	protected String doInBackground(Void... arg0) {
		SoapPrimitive soapresponse = null;
		Iterator iter = wsProperty.iterator();
		this.request = new SoapObject(WSDL_TARGET_NAMESPACE, OPERATION_NAME);

		this.envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

		while (iter.hasNext()) {
			request.addProperty((PropertyInfo) iter.next());
		}

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.bodyOut = request;

		HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
		httpTransport.debug = true;

		try {
			httpTransport.call(SOAP_ACTION, envelope);
			response = envelope.getResponse();
			soapresponse = (SoapPrimitive) envelope.getResponse();
		} catch (Exception exception) {
			response = exception.toString();
		}
		return soapresponse != null ? soapresponse.toString() : null;
	}

	protected void onPostExecute(String result) {

		super.onPostExecute(result);
		callback.onTaskDataGetComplete(result, OPERATION_NAME);

		try{
			aProgressDialog.dismiss();
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}
