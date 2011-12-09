/*
* Handler.java
*
* Created on 08 12 2011, 11:55
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

/**
 * Processor processing incoming object.
 *
 * @author Alexey 'lh' Antonov
 * @since 2011-12-08
 */
public interface Processor<T>
{
    public void setMatcher(Object matcher);
    public void setParameter(String parameter);
    public void process(T object);
}
