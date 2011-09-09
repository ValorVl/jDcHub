package ru.sincore.TigerImpl;

public class CIDGenerator
{
	public String genCid()
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
