import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.ContentSignerException;
import org.bouncycastle.operator.DigestCalculator;

public class RestContentSigner implements ContentSigner {
    private final AlgorithmIdentifier signatureAlgorithm; 
    private final String restEndpoint;
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public RestContentSigner(AlgorithmIdentifier signatureAlgorithm, String restEndpoint) {
        this.signatureAlgorithm = signatureAlgorithm;
        this.restEndpoint = restEndpoint;
    }

    @Override
    public AlgorithmIdentifier getAlgorithmIdentifier() {
        return signatureAlgorithm;
    }

    @Override
    public OutputStream getOutputStream() {
        return buffer;
    }

    @Override
    public byte[] getSignature() throws ContentSignerException {
        try {
            byte[] data = buffer.toByteArray();

            URL url = new URL(restEndpoint);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/octet-stream"); // Important for binary data 

            try (OutputStream out = conn.getOutputStream()) {
                out.write(data);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream in = conn.getInputStream()) {
                    return readSignatureFromResponse(in);
                }
            } else {
                throw new ContentSignerException("Remote signer error: HTTP " + responseCode);
            }

        } catch (IOException e) {
            throw new ContentSignerException("REST communication error", e);
        }
    }

    // Helper to read the signature bytes from the response stream
    private byte[] readSignatureFromResponse(InputStream in) throws IOException {
        // Implement reading the signature bytes from the InputStream 
        // (adjust based on how remote signer returns the signature)
    }
}
