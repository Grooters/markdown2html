import java.io.*;

public class Main {

    private final String ROOT_PATH=System.getProperty("user.dir");
    private final String OUT_PATH=ROOT_PATH+File.separator+"preview";
    private final String TARGET_PATH=getConfig();

    public static void main(String...args){

        Main mdToHt=new Main();

        String fileName=mdToHt.toHtml();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if(fileName!=null){
            File fileDir=new File(mdToHt.TARGET_PATH+File.separator+fileName);
            fileDir.mkdir();
        }

        mdToHt.outResult(mdToHt.OUT_PATH,mdToHt.TARGET_PATH+File.separator+fileName);

    }

    private String toHtml(){
        File fileDir=new File(ROOT_PATH);
        File[] files=fileDir.listFiles();
        if(files==null){
            System.err.print("目录为空");
            return null;
        }
        String fileName=null;
        String[] name;
        for(File file:files){
            fileName=file.getName();
            name=fileName.split("\\.");
            if(name.length==2&&name[1].equals("md")){
                execute("cmd.exe /c i5ting_toc -f "+fileName+" -o");
                fileName=name[0];
                break;
            }
        }
        return fileName;
    }

    private void execute(String order){
        Runtime rt = Runtime.getRuntime();
        try {
            rt.exec(order);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getConfig(){
        File config=new File(ROOT_PATH+"\\config.ini");
        if(!config.exists()){
            System.err.println("配置文件缺失");
            return null;
        }
        String outPath=null;
        try {
            BufferedReader reader= new BufferedReader(new FileReader(config));
            outPath=reader.readLine();
            if(reader.readLine()!=null && reader.readLine().equals("true")){
                execute("npm install -g i5ting_toc");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(outPath==null){
            System.err.println("请先配置输出目录");
            return null;
        }
        System.out.println("outPath:"+outPath);
        return outPath;
    }

    private void outResult(String currentSource,String currentPath){
        File fileDir=new File(currentSource);
        File[] results=fileDir.listFiles();
        if(results==null){
            System.err.println("生成目录为空");
            return;
        }
        if(TARGET_PATH==null){
            System.err.println("请先配置输出目录");
            return;
        }
        for(File file:results){
            if(file.isDirectory()){
                File targetDir=new File(currentPath+File.separator+file.getName());
                if(!targetDir.exists()){
                    targetDir.mkdir();
                }
                outResult(currentSource+File.separator+file.getName(),currentPath+File.separator+file.getName());
            }else {
                moveFile(currentPath,file);
            }
        }
    }

    private void moveFile(String currentPath,File file){
        int len;
        byte[] bytes=new byte[1024*8];
        try {
            FileInputStream fileInput=new FileInputStream(file);
            FileOutputStream fileOut=new FileOutputStream(new File(currentPath+"\\"+file.getName()));
            System.out.println("fileName:"+file.getName());
            while((len=fileInput.read(bytes))!=-1){
                fileOut.write(bytes,0,len);
            }
            fileInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
