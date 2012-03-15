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
 * ADC features
 *
 * @author hatred
 *         <p/>
 *         Date: 05.10.11
 *         Time: 14:16
 */
public class Features
{
    public static final String BASE  = "BASE"; /** BASE: Basic configuration (required by all clients) */
    public static final String BAS0  = "BAS0"; /** BAS0: Obsolete pre-ADC/1.0 protocol version */
    public static final String ADC0  = "ADC0";
    public static final String AUTO  = "AUT0"; /** AUT0: Automatic nat detection traversal */
    public static final String UCM0  = "UCM0";
    public static final String UCMD  = "UCMD"; /** UCMD: User commands */
    public static final String ZLIF  = "ZLIF"; /** ZLIF: gzip stream compression */
    public static final String TIGER = "TIGR"; /** TIGR: Client supports the tiger hash algorithm */
    public static final String BLOOM = "BLO0"; /** BLO0: Bloom filter */
    public static final String LINK  = "LINK"; /** LINK: Hub link */
    public static final String ADCS  = "ADCS"; /** ADCS: ADC over TLS/SSL */
    public static final String TCP4  = "TCP4"; /** TCP4: Indicates client supports TCPv4 connections */
    public static final String TCP6  = "TCP6"; /** TCP6: Indicates client supports TCPv6 connections */
    public static final String UDP4  = "UDP4"; /** UDP4: Indicates client supports UDPv4 connections */
    public static final String UDP6  = "UDP6"; /** UDP6: Indicates client supports UDPv6 connections */

    //extended
    public static final String PING  = "PING"; /** PING: Hub pinger information extension */
    public static final String SEGA  = "SEGA";
    public static final String BBS   = "BBS0"; /** BBS0: Bulletin board system  */
}
