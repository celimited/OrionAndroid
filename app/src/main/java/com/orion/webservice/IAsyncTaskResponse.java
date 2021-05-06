package com.orion.webservice;

public interface IAsyncTaskResponse<T> {

	public <T> void onTaskComplete(T Result);
}
