package Runner;

import com.google.common.primitives.Longs;

import java.nio.charset.StandardCharsets;

public class XOREncryptDecrypt {
    final static Long KEY = 1234312634L;

    public static byte[] EncryptDecrypt(byte[] unencryptedMsg){
        byte[] encryptedMsg = new byte[unencryptedMsg.length];
        byte[] keyBytes = Longs.toByteArray(KEY);
        int startingPos = 0;
        if(keyBytes.length > unencryptedMsg.length){
            int keyStartPosition = keyBytes.length - unencryptedMsg.length;
            for(int i = 0; i< unencryptedMsg.length; i++){
                encryptedMsg[i] = (byte) (keyBytes[keyStartPosition] ^ unencryptedMsg[i]);
            }
            return encryptedMsg;
        }


        if(keyBytes.length < unencryptedMsg.length) {
            int keyStartPosition = (unencryptedMsg.length - keyBytes.length);
            while (startingPos < keyStartPosition) {
                encryptedMsg[startingPos] = unencryptedMsg[startingPos];
                startingPos++;
            }
        }

        int keyBytesPosition = 0;
        for(int i = startingPos; i<encryptedMsg.length; i++){
            encryptedMsg[i] = (byte) (unencryptedMsg[i]^keyBytes[keyBytesPosition]);
            keyBytesPosition++;
        }

        return encryptedMsg;
    }


}
