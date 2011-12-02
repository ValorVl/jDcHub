package ru.sincore.cmd;

/*
 * jDcHub ADC HubSoft
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class CmdUtilsTest
{
	@BeforeMethod
	public void setUp() throws Exception
	{
		PropertyConfigurator.configure(ConfigurationManager.instance().getHubConfigDir() + "/log4j.properties");
	}

	@Test
	public void testStrArgToArray() throws Exception
	{
		CmdUtils cmdUtils = new CmdUtils();

		String[] args = cmdUtils.strArgToArray("--nick Valor --reason \"Long test reason\" --wrongOption wronParameter");

		System.out.println(args.length);

		for (int i = 0; i < args.length; i++)
		{
			 System.out.println("argument -> "+args[i]);
		}


	}
}
