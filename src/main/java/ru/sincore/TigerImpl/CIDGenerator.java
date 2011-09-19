/*
 * CIDGenerator.java
 *
 * Copyright (C) 2011 Viktor 'Valor' Maksimov
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
package ru.sincore.TigerImpl;

/**
 * @author Valor
 */
public class CIDGenerator
{
	public String generate()
	{
		Tiger tiger = new Tiger();
		tiger.engineReset();
		tiger.init();

		byte[] bytes = String.valueOf(System.currentTimeMillis()).getBytes();

		tiger.update(bytes,0,bytes.length);

		byte[] digestBytes = tiger.engineDigest();

		return Base32.encode(digestBytes);
	}
}
