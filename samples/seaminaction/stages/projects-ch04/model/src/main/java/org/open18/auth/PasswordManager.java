package org.open18.auth;

import java.security.MessageDigest;
import org.jboss.seam.annotations.Name;

@Name("passwordManager")
public class PasswordManager {
	private String digestAlgorithm = "SHA-1";
	private String charset = "UTF-8";

	public String getDigestAlgorithm() {
		return this.digestAlgorithm;
	}
	public void setDigestAlgorithm(String algorithm) {
		this.digestAlgorithm = algorithm;
	}

	public String getCharset() {
		return this.charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}

	public String hash(String plainTextPassword) {
		try {
			MessageDigest digest =
				MessageDigest.getInstance(digestAlgorithm);
			digest.update(plainTextPassword.getBytes(charset));
			byte[] rawHash = digest.digest();
			return new String(
				org.jboss.seam.util.Hex.encodeHex(rawHash));
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
