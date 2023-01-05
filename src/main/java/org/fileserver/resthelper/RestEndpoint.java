package org.fileserver.resthelper;

import io.netty.util.internal.SystemPropertyUtil;
import org.fileserver.HttpServer;
import org.fileserver.resthelper.annotations.Path;
import org.fileserver.resthelper.annotations.RequestType;
import org.fileserver.resthelper.dto.FileContentDto;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestEndpoint {

    @RequestType(type = RequestType.Type.Get)
    @Path("/get")
    public int get() {
        System.out.println("received request");
        return 1;
    }

    @RequestType(type = RequestType.Type.Get)
    @Path("/files")
    public List<FileContentDto> getFiles() {
        System.out.println("received request");
        List<FileContentDto> entries = new ArrayList<>();
//        Map<String, String> entry = new HashMap<>();
//        entry.put("name", "..");
//        entry.put("path", "../");
//        entries.add(entry);
        String directory = SystemPropertyUtil.get("user.dir");
        File dir = new File(directory);
        for (File f : dir.listFiles()) {
            String name = f.getName();
            FileContentDto.Type type = FileContentDto.Type.FILE;
            if (f.isDirectory()) {
                name = name + "/";
                type = FileContentDto.Type.FOLDER;
            }
            entries.add(new FileContentDto(name, f.getPath(), type));
        }
        return entries;
    }

    @RequestType(type = RequestType.Type.Get)
    @Path("/get1")
    public void get1() {
        System.out.println("received request");
    }

    @RequestType(type = RequestType.Type.Get)
    @Path("/get2")
    public String get2() {
        System.out.println("received request");
        return "abcd";
    }
}
