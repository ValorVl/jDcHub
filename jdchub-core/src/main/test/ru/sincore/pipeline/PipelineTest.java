/*
* PipelineTest.java
*
* Created on 12 12 2011, 12:08
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

package ru.sincore.pipeline;

import org.apache.log4j.PropertyConfigurator;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.sincore.adc.action.actions.MSG;
import ru.sincore.pipeline.processor.ReplaceProcessor;
import ru.sincore.util.AdcUtils;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-12-12
 */
public class PipelineTest
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
        MSG msg = new MSG("BMSG AAAA " + AdcUtils.toAdcString("Gay is fuck bitch in the ass!"));

        System.out.println(msg.getMessage());

        Pipeline<MSG> msgPipeline = new Pipeline<MSG>();

        Processor processor = new ReplaceProcessor();
        processor.setMatcher("gay");
        processor.setParameter("*");

        msgPipeline.addProcessor("replace_gay", processor);

        processor = new ReplaceProcessor();
        processor.setMatcher("fuck");
        processor.setParameter("...");

        msgPipeline.addProcessor("replace_fuck", processor);

        processor = new ReplaceProcessor();
        processor.setMatcher("bitch");
        processor.setParameter(">_<");

        msgPipeline.addProcessor("replace_bitch", processor);

        processor = new ReplaceProcessor();
        processor.setMatcher("ass");
        processor.setParameter("o_O");

        msgPipeline.addProcessor("replace_ass", processor);

        msgPipeline.process(msg);

        String message = msg.getMessage();

        // show message after processing
        System.out.println(message);

        if (!message.equals("* is ... >_< in the o_O!"))
            throw new Exception("Result string not equal to expected result!");

    }
}
