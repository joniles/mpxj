package net.sf.mpxj;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class ResourceLoader
{
    public static FileInputStream loadResource(String path) throws URISyntaxException, FileNotFoundException {
        ClassLoader classLoader = ResourceLoader.class.getClassLoader();
        URL resource = classLoader.getResource(path);
        URI uri = new URI(resource.toString());
        File file = new File(uri.getPath());

        return new FileInputStream(file);
    }
}
