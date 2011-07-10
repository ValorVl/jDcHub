package ru.sincore;

public class Port {
	
	public int portVlue;
	private boolean portStatus;
	public String MSG;
	
	public Port(int port) {
		portVlue = port;
		setStatus(true);
		MSG="Unknown";
	}
	
	public void setStatus(boolean st){
		portStatus = st;
	}
	
	public boolean getStatus(){
		return this.portStatus;
	}

}
