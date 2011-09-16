package ru.sincore.cmd.handlers;

import ru.sincore.cmd.AbstractCmd;

public class StubHandler extends AbstractCmd
{

	public void execute(String args)
	{
		System.out.println(args);
	}

}

