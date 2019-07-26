package com.example.user.markingactivity.object;

import android.content.Context;

import com.example.user.markingactivity.model.Excel_Server;

import org.json.JSONArray;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class Locations {
    private ArrayList<Project> projects;
    private ArrayList<Block> blocks;
    private Context ctx;

    public Locations(Context ctx) {
        projects = new ArrayList<Project>();
        blocks = new ArrayList<Block>();
        this.ctx = ctx;
    }

    public void setProjectsFromFiles(String path, String name) {
        File folder = new File(path);
        if (folder.exists()) {
            File[] listOfFiles = folder.listFiles();
            if (listOfFiles!=null) {
                Arrays.sort(listOfFiles);

                for (int i = 0; i < listOfFiles.length; i++) {
                    String tmpName = listOfFiles[i].getName();
                    String targetName = "";
                    if (tmpName.contains(".xlsx") && tmpName.contains(name)) {
                        targetName = tmpName.split(".xlsx")[0];
                        try {
                            if (targetName.equals(name)) {
                                targetName = "Unnamed";
                            } else if (targetName.split(name)[1].substring(0,1).equals("-")) {
                                targetName = targetName.split(name)[1].substring(1);
                            } else {
                                continue;
                            }
                            addProject(targetName);
                        } catch (Exception exx) {
                            continue;
                        }
                    }
                }
            }
        }
    }

    public void setProjectsFromServer() {
        try {
            new Excel_Server(ctx, Excel_Server.ACTION_INIT).execute().get();
            JSONArray projects = new JSONArray(new Excel_Server(ctx, Excel_Server.ACTION_GET_PROJECTS).execute().get());
            for (int i=0; i<projects.length(); i++) {
                addProject(projects.getJSONObject(i).getString("id"), projects.getJSONObject(i).getString("title"));
            }
        } catch (Exception e) {

        }
    }

    public void addProject(String id) {
        projects.add(new Project(id));
    }

    public void addProject(String id, String project_name) {
        projects.add(new Project(id, project_name));
    }

    public ArrayList<Project> getProjects() {
        return this.projects;
    }

    public Project getProject(int Index) {
        return this.projects.get(Index);
    }

    public ArrayList<String> getProjectsString() {
        ArrayList<String> tmp = new ArrayList<String>();
        for (int i=0; i<projects.size(); i++) {
            tmp.add(projects.get(i).getName());
        }
        return tmp;
    }

    public void addBlock(String block_name) {
        blocks.add(new Block(block_name));
    }

    public ArrayList<Block> getBlocks() {
        return blocks;
    }

    public Block getBlock(int index) {
        if (index < blocks.size())
            return blocks.get(index);
        else return null;
    }

    public void setBlock(int index, String block_name) {
        Block tmpBlock = blocks.get(index);
        tmpBlock.setName(block_name);
        blocks.set(index, tmpBlock);
    }

    public ArrayList<String> getBlocksString() {
        ArrayList<String> tmp = new ArrayList<String>();
        for (int i=0; i<blocks.size(); i++) {
            tmp.add(blocks.get(i).getName());
        }
        return tmp;
    }
    public String toString() {
        return blocks.toString();
    }
}