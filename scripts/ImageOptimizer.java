import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;

/**
 * Resizes and recompresses drawable PNGs to shrink APK size.
 * - Non-alpha images become JPEG (quality 82) with max long edge 1200 px.
 * - Alpha images stay PNG with the same max edge and high compression.
 */
public class ImageOptimizer {
    private static final Path DRAWABLE_DIR = Paths.get("app/src/main/res/drawable");
    private static final int MAX_LONG_EDGE = 1200;
    private static final float JPEG_QUALITY = 0.82f;
    private static final Set<String> FORCE_JPEG = Set.of(
            "cups02", "cups06" // flatten alpha if not needed
    );

    public static void main(String[] args) throws Exception {
        System.setProperty("java.awt.headless", "true");
        List<Path> pngFiles = Files.list(DRAWABLE_DIR)
                .filter(p -> p.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".png"))
                .sorted()
                .toList();

        long beforeTotal = totalSize(pngFiles);

        for (Path png : pngFiles) {
            processImage(png);
        }

        List<Path> optimized = Files.list(DRAWABLE_DIR)
                .filter(ImageOptimizer::isDrawable)
                .toList();
        long afterTotal = totalSize(optimized);

        System.out.printf(
                "Optimized %d images. Total before: %.2f MB, after: %.2f MB, savings: %.2f MB%n",
                pngFiles.size(), toMb(beforeTotal), toMb(afterTotal), toMb(beforeTotal - afterTotal)
        );
    }

    private static boolean isDrawable(Path path) {
        String name = path.getFileName().toString().toLowerCase(Locale.ROOT);
        return name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg");
    }

    private static void processImage(Path source) throws IOException {
        BufferedImage input = ImageIO.read(source.toFile());
        if (input == null) {
            System.err.println("Skipping unreadable: " + source);
            return;
        }

        String baseName = baseName(source);
        boolean forceJpeg = FORCE_JPEG.contains(baseName);
        boolean hasAlpha = !forceJpeg && input.getColorModel().hasAlpha();
        Dimension targetSize = computeTargetSize(input.getWidth(), input.getHeight());
        BufferedImage scaled = new BufferedImage(
                targetSize.width,
                targetSize.height,
                hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (!hasAlpha) {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, targetSize.width, targetSize.height);
        }
        g.drawImage(input, 0, 0, targetSize.width, targetSize.height, null);
        g.dispose();

        if (hasAlpha) {
            writePngReplacing(source, scaled);
        } else {
            Path jpgPath = replaceExtension(source, ".jpg");
            writeJpegReplacing(source, jpgPath, scaled);
        }
    }

    private static Dimension computeTargetSize(int width, int height) {
        int longEdge = Math.max(width, height);
        if (longEdge <= MAX_LONG_EDGE) {
            return new Dimension(width, height);
        }
        double scale = MAX_LONG_EDGE / (double) longEdge;
        int targetW = Math.max(1, (int) Math.round(width * scale));
        int targetH = Math.max(1, (int) Math.round(height * scale));
        return new Dimension(targetW, targetH);
    }

    private static void writeJpegReplacing(Path originalPng, Path jpgTarget, BufferedImage image) throws IOException {
        Path tmp = Files.createTempFile("imgopt-", ".jpg");
        ImageWriter writer = ImageIO.getImageWritersByFormatName("jpeg").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(JPEG_QUALITY);

        try (FileImageOutputStream out = new FileImageOutputStream(tmp.toFile())) {
            writer.setOutput(out);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }

        Files.deleteIfExists(originalPng);
        Files.move(tmp, jpgTarget, StandardCopyOption.REPLACE_EXISTING);
        System.out.printf("Converted %s -> %s (%.2f%% size target)\n",
                originalPng.getFileName(), jpgTarget.getFileName(), JPEG_QUALITY * 100);
    }

    private static void writePngReplacing(Path source, BufferedImage image) throws IOException {
        Path tmp = Files.createTempFile("imgopt-", ".png");
        ImageWriter writer = ImageIO.getImageWritersByFormatName("png").next();
        ImageWriteParam param = writer.getDefaultWriteParam();
        if (param.canWriteCompressed()) {
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(0.9f);
        }

        try (FileImageOutputStream out = new FileImageOutputStream(tmp.toFile())) {
            writer.setOutput(out);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }

        Files.move(tmp, source, StandardCopyOption.REPLACE_EXISTING);
        System.out.printf("Re-encoded %s (kept alpha)\n", source.getFileName());
    }

    private static Path replaceExtension(Path source, String newExtension) {
        String name = baseName(source);
        return source.resolveSibling(name + newExtension);
    }

    private static String baseName(Path source) {
        String name = source.getFileName().toString();
        int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            name = name.substring(0, dot);
        }
        return name;
    }

    private static long totalSize(List<Path> files) throws IOException {
        long total = 0L;
        for (Path p : files) {
            total += Files.size(p);
        }
        return total;
    }

    private static double toMb(long bytes) {
        return bytes / (1024.0 * 1024.0);
    }
}
