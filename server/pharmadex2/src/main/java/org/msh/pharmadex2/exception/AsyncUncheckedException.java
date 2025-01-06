package org.msh.pharmadex2.exception;

import java.lang.reflect.Method;

import org.msh.pharmadex2.service.r2.AsyncService;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

public class AsyncUncheckedException implements AsyncUncaughtExceptionHandler {

	@Override
	public void handleUncaughtException(Throwable ex, Method method, Object... params) {
		String message="unknown error"; 
		if(ex.getMessage()!=null) {
			message=ex.getMessage();
		}
		AsyncService.writeAsyncContext(AsyncService.PROGRESS_STOP_ERROR, message);
		System.out.println(Thread.currentThread().toString());
		System.out.println(Thread.currentThread().getState());
		System.out.println(Thread.currentThread().isAlive());
		System.out.println(Thread.currentThread().isInterrupted());
		ex.printStackTrace();

	}

}
