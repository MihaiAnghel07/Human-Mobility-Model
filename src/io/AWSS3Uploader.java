package io;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

import static utils.Utils.BUCKET_NAME;
import static utils.Utils.SECRET_KEY;

public class AWSS3Uploader extends AWSS3Service {

    public AWSS3Uploader() {
        super();
    }

    public void uploadFile(String filePath, String keyName) {
        try {
            File file = new File(filePath);
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
            bufferedInputStream.mark(0);
            byte[] inputBytes = bufferedInputStream.readAllBytes();
            bufferedInputStream.reset();

            // Setare metadata pentru criptare
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);

            // Upload fișier
            PutObjectRequest request = new PutObjectRequest(BUCKET_NAME, keyName, bufferedInputStream, metadata);
            s3Client.putObject(request);

            // Generare semnătură HMAC pentru verificarea integrității
            String fileContent = new String(inputBytes, StandardCharsets.UTF_8);
            String hmacSignature = generateHmacSHA256(fileContent, SECRET_KEY);

            System.out.println("File uploaded successfully with key: " + keyName);
            System.out.println("HMAC Signature: " + hmacSignature);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
