import android.content.Context;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.KeyStore;
import java.security.SecureRandom;

public class KeystoreManager {

    private static final String KEY_ALIAS = "MyKeyAlias";
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String TRANSFORMATION = "AES/GCM/NoPadding";

    private KeyStore keyStore;

    public KeystoreManager(Context context) {
        try {
            keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_ALIAS)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_ALIAS,
                        KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build());
                keyGenerator.generateKey();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encryptData(String data) {
        try {
            SecretKey key = ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] iv = cipher.getIV();
            byte[] encryption = cipher.doFinal(data.getBytes("UTF-8"));
            byte[] encryptedData = new byte[iv.length + encryption.length];
            System.arraycopy(iv, 0, encryptedData, 0, iv.length);
            System.arraycopy(encryption, 0, encryptedData, iv.length, encryption.length);

            return Base64.encodeToString(encryptedData, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String decryptData(String encryptedData) {
        try {
            SecretKey key = ((KeyStore.SecretKeyEntry) keyStore.getEntry(KEY_ALIAS, null)).getSecretKey();
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);

            byte[] encryptedBytes = Base64.decode(encryptedData, Base64.DEFAULT);
            GCMParameterSpec spec = new GCMParameterSpec(128, encryptedBytes, 0, 12);
            cipher.init(Cipher.DECRYPT_MODE, key, spec);

            byte[] decodedData = cipher.doFinal(encryptedBytes, 12, encryptedBytes.length - 12);
            return new String(decodedData, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
