package ru.sincore.cmd;

public abstract class AbstractCmd
{
	public abstract void execute(String args);

	void validateRights()
	{
		System.out.println("Validate...");
	};



}
