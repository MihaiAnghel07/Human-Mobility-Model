package io;

import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import static utils.Utils.BUCKET_NAME;
import static utils.Utils.SECRET_KEY;

public class AWSS3Downloader extends AWSS3Service {

    public AWSS3Downloader() {
        super();
    }

    // Funcție pentru descărcare fișier de pe S3
    public void downloadAndVerifyFile(String keyName, String downloadPath, String originalHmacSignature) {
        try {
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(BUCKET_NAME, keyName));
            InputStream inputStream = s3Object.getObjectContent();

            // Citire conținut fișier pentru verificarea HMAC
            byte[] fileBytes = inputStream.readAllBytes();
            String fileContent = new String(fileBytes, StandardCharsets.UTF_8);

            // Generare semnătură HMAC pentru verificarea integrității
            String calculatedHmac = generateHmacSHA256(fileContent, SECRET_KEY);

            // Comparare semnături HMAC
            if (calculatedHmac.equals(originalHmacSignature)) {
                System.out.println("Integrity verified. File is secure.");
                // Scriere fișier la locația specificată
                Files.write(Paths.get(downloadPath), fileBytes);
            } else {
                System.out.println("Integrity check failed! File may have been altered.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
