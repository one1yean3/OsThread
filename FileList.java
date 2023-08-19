import java.io.*;
public class FileList {
    static private String[] files;
    public FileList(){
        getAllFile();
    }
    public void printAllFile(){
        for(String file : files){
            System.out.println(file);
        }
    }
    public String printAllFileString(){
        String str = "";
        for(String file : files){
            str += file;
            str += "&";
        }
        return str;
    }
    public boolean searchFile(String wantFile){
        for(String file : files){
            if(wantFile.equals(file)) {
                return true;
            } 
        }
        return false;
    }
    public void getAllFile()
    {
        try {
            File f = new File("C:\\Users\\oneda\\Desktop\\ThreadOS\\Server_Images_Videos");
            files = f.list();
            f.getName();
            // FilenameFilter filterJpg = new FilenameFilter() {
            //     public boolean accept(File f, String name)
            //     {
            //         return name.endsWith(".jpg");
            //     }
            // };
            // FilenameFilter filterMp4 = new FilenameFilter() {
            //     public boolean accept(File f, String name)
            //     {
            //         return name.endsWith(".mp4");
            //     }
            // };
            // String[] filesJpg = f.list(filterJpg);
            // String[] filesMp4 = f.list(filterMp4);
        }
        catch (Exception e) { 
            e.printStackTrace();
        }
    }
}