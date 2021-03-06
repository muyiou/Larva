package com.runbox.debug.manager;

import com.sun.jdi.*;
import com.sun.jdi.event.*;
import com.sun.jdi.request.*;

import com.runbox.debug.command.Command;
import com.runbox.debug.command.clazz.ClassCommand;
import com.runbox.debug.manager.BreakpointManager;

public class RequestManager extends Manager {

    private RequestManager() {
        manager = MachineManager.instance().eventRequestManager();
    }

    private static RequestManager instance = new RequestManager(); 

    public static RequestManager instance() {        
        return instance;
    }

    private EventRequestManager manager = null;

    public static EventRequestManager get() {
        return instance.manager;
    }

    public ClassPrepareRequest createClassPrepareRequest(String clazz, int suspend) {
        if (null != manager) {
            ClassPrepareRequest request = manager.createClassPrepareRequest();            
			request.setSuspendPolicy(suspend);
            if (null != clazz)  {
                request.addClassFilter(clazz);
                request.putProperty(ClassCommand.CLASS, clazz);
            }
			request.enable();
            return request;
        }
        return null;
    }

    public ClassUnloadRequest createClassUnloadRequest(String clazz, int suspend) {
        if (null != manager) {
            ClassUnloadRequest request = manager.createClassUnloadRequest();
            request.setSuspendPolicy(suspend);
            if (null != clazz)  {
                request.addClassFilter(clazz);
                request.putProperty(ClassCommand.CLASS, clazz);
            }
			request.enable();
            return request;
        }
        return null;
    }
    
    public EventRequest createBreakpointRequest(Location location, BreakpointManager.Breakpoint breakpoint) {
        if (null != manager) {
			if (null != location && null != breakpoint) {
				EventRequest request = manager.createBreakpointRequest(location);
				fill(request, breakpoint);
				return request;
			}
        }
        return null;
    }

	public EventRequest createBreakpointRequest(Field field, BreakpointManager.Breakpoint breakpoint) {
		if (null != manager) {
			if (null != field && null != breakpoint) {
				EventRequest request = null;
				if (breakpoint instanceof BreakpointManager.AccessBreakpoint) {
					request = manager.createAccessWatchpointRequest(field);
				} else if (breakpoint instanceof BreakpointManager.ModifyBreakpoint) {
					request = manager.createModificationWatchpointRequest(field);
				}
				if (null != request) {
					return fill(request, breakpoint);					
				}
			} 
		}
		return null;
	}

	private EventRequest fill(EventRequest request, BreakpointManager.Breakpoint breakpoint) {
        if (null != breakpoint && null != request) {
            request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
            request.putProperty(BreakpointManager.Breakpoint.OBJECT, breakpoint);
            if (null != breakpoint.routine()) {
                request.putProperty(Command.ROUTINE, breakpoint.routine());
            }
            request.setEnabled(breakpoint.status());
            breakpoint.solve(true);
            breakpoint.request(request);
        }
        return request;
    }

    public StepRequest createStepRequest(ThreadReference thread, int size, int depth) {
        if (null != manager) {
            StepRequest request = manager.createStepRequest(thread, size, depth);
			request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
			return request;
        }
        return null;
    }

    public ThreadStartRequest createThreadStartRequest() {
        if (null != manager) {
            ThreadStartRequest request = manager.createThreadStartRequest();
            request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
            request.enable();
            return request;
        }
        return null;
    }

    public ThreadDeathRequest createThreadDeathRequest() {
        if (null != manager) {
            ThreadDeathRequest request = manager.createThreadDeathRequest();
            request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
            request.enable();
            return request;
        }
        return null;
    }

    public MethodEntryRequest createMethodEntryRequest() {
        if (null != manager) {
            return manager.createMethodEntryRequest();
        }
        return null;
    }

    public MethodExitRequest createMethodExitRequest() {
        if (null != manager) {
            return manager.createMethodExitRequest();
        }
        return null;
    }

    public ExceptionRequest createExceptionRequest(ReferenceType type, boolean caught, boolean uncaught) {
        if (null != manager) {
            ExceptionRequest request = manager.createExceptionRequest(type, caught, uncaught);
            request.setSuspendPolicy(EventRequest.SUSPEND_ALL);
            request.enable();
            return request;
        }
        return null;
    }    
    public VMDeathRequest createVMDeathRequest() {
        if (null != manager) {
            return manager.createVMDeathRequest();
        }
        return null;
    }

    public void deleteEventRequest(EventRequest request) {
        if (null != manager) {
			if (null != request) {
				request.disable();
				manager.deleteEventRequest(request);
			}
        }
    }
}
