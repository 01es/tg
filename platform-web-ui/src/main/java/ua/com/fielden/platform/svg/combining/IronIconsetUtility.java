package ua.com.fielden.platform.svg.combining;

import static java.nio.file.Files.readAllBytes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Charsets;

public class IronIconsetUtility {

    private final String fileBegin;
    private final String fileEnd;
    private final String srcFolder;

    public IronIconsetUtility(final String iconsetId, final int svgWidth, final String srcFolder) {
        this.fileBegin = String.format("<link rel=\"import\" href=\"/resources/polymer/iron-icon/iron-icon.html\"> \n <link rel=\"import\" href=\"/resources/polymer/iron-iconset-svg/iron-iconset-svg.html\"> \n <iron-iconset-svg name=\"%s\" size=\"%d\"> \n <svg> \n <defs> \n", iconsetId, svgWidth);
        this.fileEnd = "</defs> \n </svg> \n </iron-iconset-svg>";
        this.srcFolder = srcFolder;
    }

    public void createSvgIconset(final String outputFile) throws IOException {
        try (OutputStream outputStream = new FileOutputStream(outputFile)) {
            outputStream.write(joinFilesContent().getBytes(Charsets.UTF_8));
        }
    }

    public String joinFilesContent() throws IOException {
        final Set<String> files = getFilesFromFolder(srcFolder);
        for (final String file : files) {
            final File fileToValidate = new File(file);
            if (fileToValidate.length() == 0) {
                throw new IllegalArgumentException("Not valid file! Src file should not be empty.");
            }
        }
        final StringBuilder joinedFilesConent = new StringBuilder().append(fileBegin);
        for (final String file : files) {
            final String fileContent = new String(readAllBytes(Paths.get(file)));
            joinedFilesConent.append(fileContent);
        }
        return joinedFilesConent.append(fileEnd).toString();
    }

    private Set<String> getFilesFromFolder(final String folder) throws IOException {
        final Set<String> srcFiles = new HashSet<String>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(folder), "*.svg")) {
            for (final Path filePath : stream) {
                srcFiles.add(filePath.toString());
            }
        } catch (final DirectoryIteratorException ex) {
            throw ex.getCause();
        }
        if (!srcFiles.iterator().hasNext()) {
            throw new IllegalArgumentException("Empty src directory!");
        }
        return srcFiles;
    }
}
