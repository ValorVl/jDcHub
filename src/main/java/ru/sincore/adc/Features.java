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
    public static final String AUTO  = "AUT0"; /** AUT0: Automatic nat detection traversal */
    public static final String BBS   = "BBS0"; /** BBS0: Bulletin board system  */
    public static final String UCM0  = "UCM0";
    public static final String UCMD  = "UCMD"; /** UCMD: User commands */
    public static final String ZLIF  = "ZLIF"; /** ZLIF: gzip stream compression */
    public static final String TIGER = "TIGR"; /** TIGR: Client supports the tiger hash algorithm */
    public static final String BLOOM = "BLO0"; /** BLO0: Bloom filter */
    public static final String PING  = "PING"; /** PING: Hub pinger information extension */
    public static final String LINK  = "LINK"; /** LINK: Hub link */
    public static final String ADCS  = "ADCS"; /** ADCS: ADC over TLS/SSL */
}
