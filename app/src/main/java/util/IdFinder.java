package util;

import java.util.ArrayList;

import main.AllFunctions;
import newdbclass.Project;

public class IdFinder {

    public static int findprojectIdByIndex(int indexOfProject) {
        ArrayList<Project> projectList = AllFunctions.getObject().getProjectList();
        return projectList.get(indexOfProject).getId();
    }
}
