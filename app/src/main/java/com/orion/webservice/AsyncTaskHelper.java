package com.orion.webservice;

import android.os.AsyncTask;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class AsyncTaskHelper extends AsyncTask<Void, Void, String> {
	private IAsyncResponse callback;
	private String WSDL_TARGET_NAMESPACE;
	private String SOAP_ADDRESS;
	private String OPERATION_NAME;
	private String SOAP_ACTION;
	private String OPERATION_PARAMETER_NAME;
	private String OPERATION_PARAMETER_VALUE;

	SoapObject request;
	SoapSerializationEnvelope envelope;
	Object response;

	public AsyncTaskHelper(Caller caller, String WebServiceLink, String WebMethodName, String WebMethodParameterName, String WebMethodParameterValue) {

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
	}

	protected void onPreExecute() {
		super.onPreExecute();

		if (OPERATION_PARAMETER_VALUE != null) {
			PropertyInfo pi = new PropertyInfo();
			pi.setName(OPERATION_PARAMETER_NAME);
			pi.setValue(OPERATION_PARAMETER_VALUE);
			pi.setType(String.class);
			request.addProperty(pi);
		}
	}

	@Override
	protected String doInBackground(Void... arg0) {
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		//envelope.addMapping(WSDL_TARGET_NAMESPACE, "Person",new Person().getClass());

		HttpTransportSE httpTransport = new HttpTransportSE(SOAP_ADDRESS);
		httpTransport.debug = true;

		try {
			httpTransport.call(SOAP_ACTION, envelope);
			SoapPrimitive response = (SoapPrimitive) envelope.getResponse();
			//response = envelope.getResponse();
		} catch (Exception exception) {
			response = exception.toString();
		}
		return response.toString();
	}

	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		callback.onTaskComplete(result, OPERATION_NAME);
	}
}
