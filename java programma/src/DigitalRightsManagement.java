import java.util.Base64;


class DigitalRightsManagement {
	final static int continueKeySize=18;
	final static int additionNumber=0;
	final static int magicNumber=0;
	static String encryptAES(String value) {
		return value;
	}
	static String decryptAES(String encrypted) {
		return encrypted;
	}

	static String base64encode(String in){
		return new String(Base64.getEncoder().encode(in.getBytes()));
	}
	static String base64decode(String in){
		return new String(Base64.getDecoder().decode(in.getBytes()));
	}
}
