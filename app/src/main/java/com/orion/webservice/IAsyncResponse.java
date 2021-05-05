package com.orion.webservice;

public interface IAsyncResponse {

	public <T> void onTaskComplete(T Result, T WebMethod);
	public <T> void onTaskDataGetComplete(T Result, T WebMethod);
}
