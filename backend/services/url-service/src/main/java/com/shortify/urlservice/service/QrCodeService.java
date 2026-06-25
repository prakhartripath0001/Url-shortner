package com.shortify.urlservice.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Generates QR codes for short URLs using the ZXing library.
 *
 * ERROR CORRECTION LEVELS (important to understand):
 * - L: ~7% data recovery  — smallest QR code, used when codes are printed cleanly
 * - M: ~15% data recovery — balanced, good for most use cases
 * - Q: ~25% data recovery — when codes might get dirty/damaged
 * - H: ~30% data recovery — logos embedded in QR code (like Bitly does)
 *
 * We use ErrorCorrectionLevel.M for balanced size and resilience.
 */
@Slf4j
@Service
public class QrCodeService {

    private static final int DEFAULT_SIZE = 300; // 300x300 pixels
    private static final String FORMAT = "PNG";

    public byte[] generateQrCode(String url) {
        return generateQrCode(url, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public byte[] generateQrCode(String url, int width, int height) {
        QRCodeWriter writer = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = Map.of(
                EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                EncodeHintType.MARGIN, 1
        );

        try {
            BitMatrix matrix = writer.encode(url, BarcodeFormat.QR_CODE, width, height, hints);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, FORMAT, outputStream);
            return outputStream.toByteArray();
        } catch (WriterException | IOException e) {
            log.error("Failed to generate QR code for URL: {}", url, e);
            throw new RuntimeException("QR code generation failed", e);
        }
    }
}
