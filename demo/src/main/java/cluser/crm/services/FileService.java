package cluser.crm.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileService {

    @Value("${UPLOAD_DIRECTORY}")
    private String uploadDirectory;

    /**
     * @param objectId     - the id of the resource for which the folder is created
     * @param objectFolder - the name of the folder to be created
     * @Description: Creates a folder
     * @Description: Creates a folder
     */
    public void createFolder(String objectFolder, String objectId) {
        File uploadDir = new File(uploadDirectory);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
            uploadDir.setWritable(true, true);
            uploadDir.setReadable(true, false);
            uploadDir.setExecutable(true, false);
        }
        /**
         * files/objectFolder/{objectId}
         */
        File objectFolderLocation = new File(uploadDirectory + File.separator + objectFolder + File.separator + objectId);
        if (!objectFolderLocation.exists()) {
            objectFolderLocation.mkdirs();
            objectFolderLocation.setWritable(true, true);
            objectFolderLocation.setReadable(true, false);
            objectFolderLocation.setExecutable(true, false);
        }
    }


}
