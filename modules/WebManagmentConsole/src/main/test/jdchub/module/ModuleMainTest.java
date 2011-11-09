/*
* WebManagementConsoleTest.java
*
* Created on 09 11 2011, 13:50
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

package jdchub.module;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Alexey 'lh' Antonov
 * @since 2011-11-09
 */
public class ModuleMainTest
{
    private ModuleMain moduleMain = null;


    @BeforeClass
    public void setUp()
            throws Exception
    {
        moduleMain = new ModuleMain();
    }


    @Test
    public void testInit()
            throws Exception
    {
        boolean initiated = moduleMain.init();
        assert initiated : "ModuleMain doesn't initiated";
    }


    @Test(dependsOnMethods = "testInit")
    public void testDeinit()
            throws Exception
    {
        boolean deinitiated = moduleMain.deinit();
        assert deinitiated : "ModuleMain doesn't deinitiated";
    }
}
