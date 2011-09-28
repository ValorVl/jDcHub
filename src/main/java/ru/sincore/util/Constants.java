/*
 * 
 *
 * Created on 19 ianuarie 2007, 23:52
 *
 * DSHub ADC HubSoft
 * Copyright (C) 2007,2008  Eugen Hristev
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

package ru.sincore.util;

/**
 * @author Pietricica
 * @author Valor
 */
public final class Constants
{
    // Hub base functionality SUP string
    public static final String HUB_BASE_SUP_STRING = "ADBASE ADTIGR ADUCM0 ADADC0";

    // STA Severity codes block
    public static final int STA_SEVERITY_SUCCESS                = 0;
    public static final int STA_SEVERITY_RECOVERABLE            = 100;
    public static final int STA_SEVERITY_FATAL                  = 200;


	// STA Error codes block
    public static final int STA_GENERIC_HUB_ERROR              	= 10;
    public static final int STA_HUB_FULL                       	= 10;
    public static final int STA_HUB_DISABLED                   	= 11;
    public static final int STA_GENERIC_LOGIN_ERROR            	= 20;
    public static final int STA_NICK_INVALID                   	= 21;
    public static final int STA_NICK_TAKEN                     	= 22;
    public static final int STA_INVALID_PASSWORD               	= 23;
    public static final int STA_CID_TAKEN                      	= 24;
    public static final int STA_ACCESS_DENIED                  	= 25;
    public static final int STA_REG_ONLY                       	= 26;
    public static final int STA_INVALID_PID                    	= 27;
    public static final int STA_GENERIC_KICK_DISCONNECT_BAN    	= 30;
    public static final int STA_PERMANENTLY_BANNED             	= 31;
    public static final int STA_TEMP_BANNED                    	= 32;
    public static final int STA_GENERIC_PROTOCOL_ERROR         	= 40;
    public static final int STA_TRANSFER_PROTOCOL_ERROR        	= 41;
    public static final int STA_DIRECT_CONNECT_FAILED          	= 42;
    public static final int STA_REQUIRED_INF_FIELD_BAD_MISSING 	= 43;
    public static final int STA_INVALID_STATE                  	= 44;
    public static final int STA_REQUIRED_FEATURE_MISSING       	= 45;
    public static final int STA_INVALID_IP                     	= 46;
    public static final int STA_NO_HASH_OVERLAP                	= 47;

	//Other const block
	public static final String EMPTY_STR 						= "";
}
