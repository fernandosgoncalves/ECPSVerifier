package parser;

import java.util.ArrayList;

public class AadlFile {
	private boolean haveRoot = false;

	private ArrayList<AadlProcessImpl> processesImpl;
	private ArrayList<AadlSystemImpl> systemsImpl;
	private ArrayList<AadlDeviceImpl> devicesImpl;
	private ArrayList<AadlThreadImpl> threadsImpl;
	private ArrayList<AadlSubprogram> subprograms;
	private ArrayList<AadlProcess> processes;
	private AadlErrorAnnexLibrary annexEmv2;	
	private ArrayList<AadlSystem> systems;
	private ArrayList<AadlDevice> devices;
	private ArrayList<AadlThread> threads;
	
	public AadlFile() {
		processesImpl = new ArrayList<>();
		systemsImpl = new ArrayList<>();
		devicesImpl = new ArrayList<>();
		threadsImpl = new ArrayList<>();
		subprograms = new ArrayList<>();
		processes = new ArrayList<>();
		systems = new ArrayList<>();
		devices = new ArrayList<>();
		threads = new ArrayList<>();
		annexEmv2 = null;
	}

	public AadlErrorAnnexLibrary getAnnexEmv2() {
		return annexEmv2;
	}

	public void setAnnexEmv2(AadlErrorAnnexLibrary annexEmv2) {
		this.annexEmv2 = annexEmv2;
	}

	public AadlSystem getSystemRoot() {
		if (!systems.isEmpty()) {
			for (AadlSystem s : systems) {
				if (s.isRoot())
					return s;
			}
		}
		return null;
	}

	public boolean isHaveRoot() {
		return haveRoot;
	}

	public void setHaveRoot(boolean haveRoot) {
		this.haveRoot = haveRoot;
	}

	public void addProcess(AadlProcess p) {
		processes.add(p);
	}

	public void updateProcessByName(AadlProcess process) {
		for (int i = 0; i < processes.size(); i++) {
			if (processes.get(i).getName().equals(process.getName()))
				processes.set(i, process);
		}
	}

	public void addDevice(AadlDevice d) {
		devices.add(d);
	}

	public void addDeviceImpl(AadlDeviceImpl d) {
		devicesImpl.add(d);
	}

	public void updateDeviceByName(AadlDevice device) {
		for (int i = 0; i < devices.size(); i++) {
			if (devices.get(i).getName().equals(device.getName()))
				devices.set(i, device);
		}
	}

	public void addThread(AadlThread t) {
		threads.add(t);
	}

	public void updateThreadByName(AadlThread thread) {
		for (int i = 0; i < threads.size(); i++) {
			if (threads.get(i).getName().equals(thread.getName()))
				threads.set(i, thread);
		}
	}

	public void addSubprogram(AadlSubprogram s) {
		subprograms.add(s);
	}

	public void updateSubprogramByName(AadlSubprogram subprogram) {
		for (int i = 0; i < subprograms.size(); i++) {
			if (subprograms.get(i).getName().equals(subprogram.getName()))
				subprograms.set(i, subprogram);
		}
	}

	public void addSystemImpl(AadlSystemImpl s) {
		systemsImpl.add(s);
	}

	public void addSystem(AadlSystem s) {
		systems.add(s);
	}

	public AadlSystem getSystemByName(String name) {
		for (AadlSystem s : systems) {
			if (s.getName().equalsIgnoreCase(name))
				return s;
		}
		return null;
	}

	public void updateSystemByName(AadlSystem system) {
		for (int i = 0; i < systems.size(); i++) {
			if (systems.get(i).getName().equalsIgnoreCase(system.getName()))
				systems.set(i, system);
		}
	}

	public boolean isSystemCreated(String name) {
		for (AadlSystem s : systems) {
			if (s.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public boolean isDeviceCreated(String name) {
		for (AadlDevice d : devices) {
			if (d.getName().equalsIgnoreCase(name))
				return true;
		}
		return false;
	}

	public void addProcessImpl(AadlProcessImpl p) {
		processesImpl.add(p);
	}

	public ArrayList<AadlProcessImpl> getProcessesImpl() {
		return processesImpl;
	}

	public void setProcessesImpl(ArrayList<AadlProcessImpl> processesImpl) {
		this.processesImpl = processesImpl;
	}

	public ArrayList<AadlSubprogram> getSubprograms() {
		return subprograms;
	}

	public void setSubprograms(ArrayList<AadlSubprogram> subprograms) {
		this.subprograms = subprograms;
	}

	public ArrayList<AadlSystemImpl> getSystemsImpl() {
		return systemsImpl;
	}

	public void setSystemsImpl(ArrayList<AadlSystemImpl> systemsImpl) {
		this.systemsImpl = systemsImpl;
	}

	public ArrayList<AadlDeviceImpl> getDevicesImpl() {
		return devicesImpl;
	}

	public void setDevicesImpl(ArrayList<AadlDeviceImpl> devicesImpl) {
		this.devicesImpl = devicesImpl;
	}

	public ArrayList<AadlThreadImpl> getThreadsImpl() {
		return threadsImpl;
	}

	public void setThreadsImpl(ArrayList<AadlThreadImpl> threadsImpl) {
		this.threadsImpl = threadsImpl;
	}

	public ArrayList<AadlProcess> getProcesses() {
		return processes;
	}

	public void setProcesses(ArrayList<AadlProcess> processes) {
		this.processes = processes;
	}

	public ArrayList<AadlSystem> getSystems() {
		return systems;
	}

	public void setSystems(ArrayList<AadlSystem> systems) {
		this.systems = systems;
	}

	public ArrayList<AadlDevice> getDevices() {
		return devices;
	}

	public void setDevices(ArrayList<AadlDevice> devices) {
		this.devices = devices;
	}

	public void addThreadImpl(AadlThreadImpl thread) {
		threadsImpl.add(thread);
	}

	public ArrayList<AadlThread> getThreads() {
		return threads;
	}

	public void setThreads(ArrayList<AadlThread> threads) {
		this.threads = threads;
	}

	public AadlThread getThreadByName(String name){
		for(AadlThread t : threads){
			if(t.getName().equalsIgnoreCase(name))
				return t;
		}
		return null;
	}
}
