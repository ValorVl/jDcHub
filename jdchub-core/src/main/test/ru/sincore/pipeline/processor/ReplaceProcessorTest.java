/*
* ReplaceProcessorTest.java
*
* Created on 12 12 2011, 10:36
*
* Copyright (C) 2011 Alexey 'lh' Antonov
*
* This program is free software; you can redistribute it and/or
* modify it under the terms of the GNU General Public License
* as published by the Free Software Foundation; either version 2
* of the License, or any later version.

* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.

* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/

package ru.sincore.pipeline.processor;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.ConfigurationManager;
import ru.sincore.adc.action.actions.MSG;
import ru.sincore.pipeline.Processor;
import ru.sincore.util.AdcUtils;

import java.lang.String;
import java.lang.System;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-12-12
 */
public class ReplaceProcessorTest
{
    @BeforeMethod
    public void setUp()
            throws Exception
    {
        PropertyConfigurator.configure("./jdchub-core/etc/log4j.properties");
    }


    @Test
    public void testProcess()
            throws Exception
    {
        Processor processor = new ReplaceProcessor();
        
        processor.setMatcher("foo");
        processor.setParameter("*");

        MSG msg = new MSG("BMSG AAAA " + AdcUtils.toAdcString("Mega foo message foo foo!"));

        // show message befor processing
        System.out.println(msg.getMessage());

        processor.process(msg);

        String message = msg.getMessage();

        // show message after processing
        System.out.println(message);

        boolean result = message.equals("Mega * message * *!");

        assert  result == true : "Result string not equal to expected result!";
    }
}
