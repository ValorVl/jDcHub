/*
 * jDcHub
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

package ru.sincore.adc;

/**
 * ADC command flags
 *
 * @author Alexander 'hatred' Drozdov
 *         <p/>
 *         Date: 25.11.11
 *         Time: 8:28
 */
public class Flags
{
    //
    // INF flags
    //
    public static final String NICK                     = "NI";
    public static final String CID                      = "ID";
    public static final String PID                      = "PD";
    public static final String ADDR_IPV4                = "I4";
    public static final String ADDR_IPV6                = "I6";
    public static final String UDP_PORT_IPV4            = "U4";
    public static final String UDP_PORT_IPV6            = "U6";
    public static final String SHARE_SIZE               = "SS";
    public static final String SHARED_FILES             = "SF";
    public static final String VERSION                  = "VE";
    public static final String MAX_UPLOAD_SPEED         = "US";
    public static final String MAX_DOWNLOAD_SPEED       = "DS";
    public static final String OPENED_UPLOAD_SLOTS = "SL";
    public static final String AUTOMATIC_SLOT_ALLOCATOR = "AS";
    public static final String MIN_AUTOMATIC_SLOTS = "AM";
    public static final String EMAIL                    = "EM";
    public static final String DESCRIPTION              = "DE";
    public static final String AMOUNT_HUBS_WHERE_NORMAL_USER = "HN";
    public static final String AMOUNT_HUBS_WHERE_REGISTERED_USER = "HR";
    public static final String AMOUNT_HUBS_WHERE_OP_USER = "HO";
    public static final String TOKEN                    = "TO";
    public static final String CLIENT_TYPE              = "CT";
    public static final String AWAY                     = "AW";
    public static final String FEATURES                 = "SU";
    public static final String REFERER_URL              = "RF";
    public static final String HUB_ITSELF               = "HU";
    // Known extensions
    public static final String HIDDEN                   = "HI";
    public static final String LOCALE                   = "LC";
    // PING extension flags for INF
    public static final String HUB_HOST                 = "HH";
    public static final String HUB_WEBSITE              = "WS";
    public static final String HUB_NETWORK              = "NE";
    public static final String HUB_OWNER_NAME           = "OW";
    public static final String HUB_USERS_ONLINE = "UC";
    public static final String HUB_TOTAL_SHARE_SIZE     = "SS";
    public static final String HUB_TOTAL_SHARED_FILES   = "SF";
    public static final String HUB_MIN_ALLOWED_SHARE_SIZE = "MS";
    public static final String HUB_MAX_ALLOWED_SHARE_SIZE = "XS";
    public static final String HUB_MIN_ALLOWED_SLOTS      = "ML";
    public static final String HUB_MAX_ALLOWED_SLOTS      = "XL";
    public static final String HUB_MIN_AMOUNT_HUBS_WHERE_NORMAL_USER = "MU";
    public static final String HUB_MIN_AMOUNT_HUBS_WHERE_REGISTERED_USER = "MR";
    public static final String HUB_MIN_AMOUNT_HUBS_WHERE_OP = "MO";
    public static final String HUB_MAX_AMOUNT_HUBS_WHERE_NORMAL_USER = "XU";
    public static final String HUB_MAX_AMOUNT_HUBS_WHERE_REGISTERED_USER = "XR";
    public static final String HUB_MAX_AMOUNT_HUBS_WHERE_OP = "XO";
    public static final String HUB_MAX_ALLOWED_USERS        = "MC";
    public static final String HUB_UPTIME                   = "UP";


    //
    // SCH flags
    //
    public static final String SCH_INCLUDE = "AN";
    public static final String SCH_EXCLUDE = "NO";
    public static final String SCH_EXTENSION = "EX";
    public static final String SCH_LESS_THAN = "LE";
    public static final String SCH_GREATER_THAN = "GR";
    public static final String SCH_EXACT_SIZE = "EQ";
    //public static final String SCH_TOKEN      = TOKEN;
    public static final String SCH_FILE_TYPE = "TY";
    // SEGA extension
    public static final String SCH_SEGA_GROUP = "GR";
    public static final String SCH_SEGA_EXCLUDE = "RX";
}
