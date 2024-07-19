package me.Ult1;

//import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
//import com.google.api.client.googleapis.json.GoogleJsonResponseException;
//import com.google.api.client.http.InputStreamContent;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.JsonFactory;
//import com.google.api.client.json.jackson2.JacksonFactory;
//
//import com.google.api.services.youtube.YouTube;
//import com.google.api.services.youtube.model.Video;
//import com.google.api.services.youtube.model.VideoSnippet;
//import com.google.api.services.youtube.model.VideoStatus;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Main {

    static File folder;
    static boolean killedAutomatically = false;

    public static void main(String[] args) throws IOException, InterruptedException {
        folder = new File(args[0]); // See, this is how you do it "c"...
        if(!folder.exists()) System.err.println("Folder at path: " + args[0] + " doesn't exist!");

        int currentFileNumber = 0;
//        List<File> files = Arrays.stream(folder.listFiles()).toList();
//        Collections.reverse(files);
        File[] fileList = folder.listFiles();
        if(args.length == 2 && args[1].equalsIgnoreCase("r")) {
            System.out.println("Files will be given in a reverse order!");
            fileList = reverse(fileList);
        }

        mainLoop:
        for(File file : fileList){ // See, this how you do it "windows.h"...
            currentFileNumber ++;
            // FILE PROPERTIES*
            String title = null, description = null;

            // TIME
            Process timeChecker = Runtime.getRuntime().exec("ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1" + " \"" + file.getAbsolutePath() + "\"");
            BufferedReader timeOutputStream = new BufferedReader( new InputStreamReader( timeChecker.getInputStream() ) );
            String time = timeOutputStream.readLine();
            if(time == null) time = "60.0";

            // START "WINDOWS MEDIA PLAYER CLASSIC"
            System.out.println("file: \"" + file.getName() + "\"" + ", fileCount: " + currentFileNumber); // IT DOESN'T WORK IF I LET IT TURN OFF BY IT SELF
            ProcessBuilder pb = new ProcessBuilder("C:\\Program Files (x86)\\K-Lite Codec Pack\\MPC-HC64\\mpc-hc64.exe", file.getAbsolutePath());
            Process mpc = pb.start();
            Thread thisThread = Thread.currentThread();
            killedAutomatically = false;

            new Thread(() -> {
                while (mpc.isAlive()) ;
                if (!killedAutomatically) {
                    thisThread.interrupt();
                    thisThread.start();
                }
            }).start();

            if(thisThread.isInterrupted()) {
                thisThread.start();
            }

            try {
                Thread.sleep((long) (Math.floor(Double.parseDouble(time)) * 1000 + 1000));
            } catch (InterruptedException ignored) {
            }

            if( mpc.isAlive()) {
                killedAutomatically = true;
                mpc.destroy();
            }


            // CONSOLE AFTER WATCHING THRU
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            File newFile = null;
            boolean bool = true;
            while( bool ){

                bool = false;
                System.out.print("save video? (help) : ");
                String input = reader.readLine();

                if (input.toLowerCase().contains("y")) { // yes
                    boolean uniqueTitle = false;
                    while(!uniqueTitle) {
                        uniqueTitle = true;
                        System.out.print("title: ");
                        title = reader.readLine();
                        for(File f : new File("D:\\Ult1\\videos_RandomVideos\\Edit-Clip-Videos").listFiles())
//                            System.out.println(f.getName() + ", " + title);
                            if(f.getName().equalsIgnoreCase(title + ".mp4")){
                                uniqueTitle = false;
                                System.out.println("Please give a unique title!");
                                break;
                            }
                    }

//                    System.out.print("desc: ");
//                    description = reader.readLine();

                    newFile = new File(String.format("D:\\Ult1\\videos_RandomVideos\\Edit-Clip-Videos\\%s.mp4", title));

                    boolean keepAudio = false;

                    System.out.print("params(a/d/-): ");
                    String params = reader.readLine();
                    if(params.toLowerCase().contains("a")) // keep audio
//                        Runtime.getRuntime().exec(String.format("ffmpeg -i \"%s\" -an -c:v copy \"%s\"", newFile.getAbsolutePath(), newFile.getAbsolutePath()));
                        keepAudio = true;
                    if(params.toLowerCase().contains("d")) // delete file
                        file.delete();

                    System.out.print("edit(y/n): ");
                    String edit = reader.readLine();
                    if(edit.equalsIgnoreCase("y")){ // (y)es to edit
                        System.out.println("Video length: " + time);
                        System.out.print("from SS: ");
                        String from = reader.readLine(); if(from.isEmpty()) from = "00";
                        System.out.print("to   SS: ");
                        String to   = reader.readLine(); if(to.isEmpty()  ) to   = time; // VOLATILE !!!!! ########################################################################################
                        if(from.contains(":") || to.contains(":"))
                            if(keepAudio)
                                Runtime.getRuntime().exec(String.format("ffmpeg -ss %s -to %s -i %s -c copy %s", from, to, file.getAbsolutePath(), newFile.getAbsolutePath()));
                            else
                                Runtime.getRuntime().exec(String.format("ffmpeg -ss %s -to %s -i %s -an -c copy %s", from, to, file.getAbsolutePath(), newFile.getAbsolutePath()));
                        else if(!from.equalsIgnoreCase("-") || !from.isEmpty() || !to.equalsIgnoreCase("-") || !to.isEmpty())
                            if(keepAudio)
                                Runtime.getRuntime().exec(String.format("ffmpeg -ss 00:00:%s -to 00:00:%s -i %s -c copy %s", from, to, file.getAbsolutePath(), newFile.getAbsolutePath()));
                            else
                                Runtime.getRuntime().exec(String.format("ffmpeg -ss 00:00:%s -to 00:00:%s -i %s -an -c copy %s", from, to, file.getAbsolutePath(), newFile.getAbsolutePath()));
                        else
                            Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    } else
                        Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);



                } // y

                if(input.toLowerCase().contains("n")) continue mainLoop; // no
                if(input.toLowerCase().contains("r")) // rewatch
                    System.out.println("I might implement this at some point... I have a for-loop right now sooooo");;
                if(input.equalsIgnoreCase("help")) {
                    System.out.println(
                            "    |-----------------------| HELP |-----------------------  \n" +
                            "    | y - approves video (yes)      \n " +
                            "    |    title - sets video title  \n" +
                            "    |    desc  - sets video description (description) \n" +
                            "    |    a - keep audio (audio)" +
                            "    |    d - delete file (delete)" +
                            "    |    edit - y/n if you want to edit the video" +
                            "    |      from - HH:MM:SS   default(HH:MM == 00:00)" +
                            "    |      to   - HH:MM:SS   default(HH:MM == 00:00)" +
                            "    | n - rejects video (no )      \n" +
                            "    | r - rewatches video (rewatch) (WIP)\n" +
                            "    | q - quits program (quit)    \n" +
                            "    | help - gives this info       \n" +
                            "    |------------------------------------------------------  \n"
                    );
                    bool = true;
                }
                if(input.toLowerCase().contains("q")) // exit
                    break mainLoop; // just in case it could be this, just, because I don't like this in JS & C.
            }
        } // NEXT FILE


    }

    static File[] reverse(File files[]) {
        int i;
        File t;
        for (i = 0; i < files.length / 2; i++) {
            t = files[i];
            files[i] = files[files.length - i - 1];
            files[files.length - i - 1] = t;
        }
        return files;
    }
}


// TODO
//  go thru clips                               DONE
//  give video to watch in MPC                  DONE
//  close MPC (after 60s)                       DONE
//  cin >> if want to save                      DONE
//  true =         ask for title & description  DONE
//  true =         trim audio                   DONE    ffmpeg -i "$file" -an -c:v copy "${file%.mp4}_noaudio.mp4" <-- edit // https://superuser.com/questions/1539280
//  true =         upload to yt w/youtube API
//  true =         edit video                   DONE    ffmpeg -ss 00:01:00 -to 00:02:00  -i input.mp4 -c copy output.mp4
//  true & false = delete video                 DONE
//  for-loop to the next video                  DONE
//  might be issue with colliding titles        DONE

// started 3:20 pm monday