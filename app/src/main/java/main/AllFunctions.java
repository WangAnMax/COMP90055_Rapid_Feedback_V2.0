/**
 * Created by: Android frontend team
 *
 * Team Member: Wang AN, NingJiang XIE
 */

package main;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import newdbclass.Criterion;
import newdbclass.Project;
import newdbclass.ProjectStudent;
import newdbclass.Remark;
import newdbclass.Student;
import util.ExcelParser;

public class AllFunctions {

    private static AllFunctions allFunctions;
    //initiate the new object: AllFunctions all = AllFunctions.getObject();

    private CommunicationForClient communication;
    private ArrayList<Project> projectList = new ArrayList<Project>();
    private Handler handlerAllfunction;
    private String username;//for welcome message. this is the firstName.
    private String userEmail;
    private int userId;

    private AllFunctions() {
        communication = new CommunicationForClient(this);
    }

    public void setHandler(Handler hander) {
        handlerAllfunction = hander;
    }

    public void exceptionWithServer() {
        System.out.println("Communication error.");
    }

    static public AllFunctions getObject() {
        if (allFunctions == null) {
            allFunctions = new AllFunctions();
        }
        return allFunctions;
    }

    public void register(final String firstName, final String middleName,
                         final String lastName, final String email,
                         final String password) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.register(firstName, middleName, lastName, email, password);
            }
        }).start();
    }

    public void registerACK(int ack) {
        if (ack == 0) // server error
            handlerAllfunction.sendEmptyMessage(101);
        else if (ack == -1) // email exists
            handlerAllfunction.sendEmptyMessage(102);
        else if (ack > 0) // register success
            handlerAllfunction.sendEmptyMessage(103);
    }

    public void login(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.login(username, password);
            }
        }).start();
    }

    public void loginACK(int ack) {
        if (ack > 0) // login success
            handlerAllfunction.sendEmptyMessage(104);
        else if (ack == 0) // wrong password
            handlerAllfunction.sendEmptyMessage(105);
        else if (ack == -1) // email not registered
            handlerAllfunction.sendEmptyMessage(106);
        else if (ack == -2) // server error
            handlerAllfunction.sendEmptyMessage(107);
    }

    //    public void submitRecorder() {
//        communication.submitFile();
//    }
//
    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    public String getUserEmail() {
        return this.userEmail;
    }

    public void setId(int id) {
        this.userId = id;
    }

    public int getId() {
        return this.userId;
    }

    public ArrayList<Project> getProjectList() {
        return projectList;
    }

    public void syncProjectList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.syncProjectList(userId);
            }
        }).start();
    }

    public void syncACK(boolean ack, ArrayList<Project> projectList) {
        if (ack) { // sync success
            this.projectList = projectList;
            if (projectList.size() > 0) {
                sortStudent();
            }
            handlerAllfunction.sendEmptyMessage(108);
        } else { // succ fail
            handlerAllfunction.sendEmptyMessage(109);
        }
    }

    public void updateProject(String projectName, String subjectName,
                              String subjectCode, String description,
                              int durationSec, int warningSec, int projectId) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.updateProjectAbout(projectName, subjectName,
                        subjectCode, description, durationSec, warningSec, userId, projectId);
            }
        }).start();
    }

    public void setAboutACK(boolean ack, int projectId) {
        if (ack) {
            handlerAllfunction.sendEmptyMessage(110);
        } else {
            handlerAllfunction.sendEmptyMessage(111);
        }
    }

    public void deleteProject(int index, int projectId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.deleteProject(projectId);
            }
        }).start();
        projectList.remove(index);
    }

    public void deleteACK(boolean ack) {
        if (ack) {
            handlerAllfunction.sendEmptyMessage(112);
        } else {
            handlerAllfunction.sendEmptyMessage(113);
        }
    }

    public void updateProjectCriteria(ArrayList<Criterion> criteriaList, int projectId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.criteriaListSend(criteriaList, projectId);
            }
        }).start();
    }

    public void updateProjectCriteriaACK(boolean ack) {
        if (ack) {
            handlerAllfunction.sendEmptyMessage(114);
        } else {
            handlerAllfunction.sendEmptyMessage(115);
        }
    }

    public void inviteMarker(int markerId, int projectId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.inviteMarker(markerId, projectId);
            }
        }).start();
    }

    public void inviteMarkerACK(int ack) {
        if (ack == 1) { // add successfully
            handlerAllfunction.sendEmptyMessage(116);
        } else if (ack == 0) { // SQL exception
            handlerAllfunction.sendEmptyMessage(117);
        } else if (ack == -1) { // marker id invalid
            handlerAllfunction.sendEmptyMessage(118);
        }
    }

    public void deleteMarker(int markerId, int projectId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.deleteMarker(markerId, projectId);
            }
        }).start();
    }

    public void deleteMarkerACK(boolean ack) {
        if (ack) {
            handlerAllfunction.sendEmptyMessage(119);
        } else {
            handlerAllfunction.sendEmptyMessage(120);
        }
    }

    public boolean hasStudent(int projectId, int studentId) {
        Project project = new Project();
        for (int i = 0; i < this.projectList.size(); i++) {
            if (projectList.get(i).getId() == projectId) {
                project = projectList.get(i);
            }
        }
        ArrayList<ProjectStudent> studentList = project.getStudentList();
        for (int i = 0; i < studentList.size(); i++) {
            if (studentList.get(i).getStudentNumber() == studentId) {
                return true;
            }
        }
        return false;
    }

    public void addStudent(int projectId, ArrayList<Student> studentList, String action) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.addStudent(projectId, studentList, action);
            }
        }).start();
    }

    public void addStudentACK(int ack, String action) {
        if (ack == 1) { // add student success
            if (action.equals("single")) {
                Message message = new Message();
                message.what = 121;
                message.obj = action;
                handlerAllfunction.sendMessage(message);
            } else if (action.equals("batch")) {
                Message message = new Message();
                message.what = 121;
                message.obj = action;
                handlerAllfunction.sendMessage(message);
            }
        } else if (ack == 0) { // SQL exception
            handlerAllfunction.sendEmptyMessage(122);
        } else if (ack < 0) { // add student fail
            Message message = new Message();
            message.what = 123;
            message.arg1 = -ack;
            handlerAllfunction.sendMessage(message);
        }
    }

    public void editStudent(int studentId, int studentNumber, String firstName,
                            String middleName, String surname, String email, int groupNumber, int projectId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.editStudent(studentId, studentNumber, firstName,
                        middleName, surname, email, groupNumber, projectId);
            }
        }).start();
    }

    public void editStudentACK(boolean ack) {
        if (ack) {
            handlerAllfunction.sendEmptyMessage(124);
        } else {
            handlerAllfunction.sendEmptyMessage(125);
        }
    }

    public void deleteStudent(int projectId, int studentId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.deleteStudent(projectId, studentId);
            }
        }).start();
    }

    public void deleteStudentACK(boolean ack) {
        if (ack) {
            handlerAllfunction.sendEmptyMessage(126);
        } else {
            handlerAllfunction.sendEmptyMessage(127);
        }
    }

    public ArrayList<Criterion> readCriteriaExcel(Project project, String path) {
        ExcelParser excelParser = new ExcelParser();
        if (path.endsWith(".xls")) {
            return excelParser.readXlsCriteria(path);
        } else if (path.endsWith(".xlsx")) {
            return excelParser.readXlsxCriteria(path);
        }
        return null;
    }

    public void sendMark(int projectId, int studentId, String remark) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.sendMark(projectId, studentId, remark);
            }
        }).start();
    }

    public void sendMarkACK(String ack) {
        if (ack.equals("true")) {
            handlerAllfunction.sendEmptyMessage(128);
        } else {
            handlerAllfunction.sendEmptyMessage(129);
        }
    }

    public void sendEmail(int projectId, int studentId, int sendBoth) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.sendEmail(projectId, studentId, sendBoth);
            }
        }).start();
    }

    public void sendFinalResult(int projectId, int studentId, double finalScore, String finalRemark) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.sendFinalResult(projectId, studentId, finalScore, finalRemark);
            }
        }).start();
    }

    public void sendFinalResultACK(boolean ack) {
        if (ack) {
            handlerAllfunction.sendEmptyMessage(130);
        } else {
            handlerAllfunction.sendEmptyMessage(131);
        }
    }

    public void submitRecorder(int projectId, int studentId, String length) {
        communication.submitFile(projectId, studentId, length);
    }

    public void sortStudent() {
        for (int i = 0; i < this.projectList.size(); i++) {
            Collections.sort(this.projectList.get(i).getStudentList(), new SortByGroup());
        }
    }

    public class SortByGroup implements Comparator {
        public int compare(Object o1, Object o2) {
            ProjectStudent s1 = (ProjectStudent) o1;
            ProjectStudent s2 = (ProjectStudent) o2;
            if (s1.getGroupNumber() > s2.getGroupNumber() && s2.getGroupNumber() == 0) {
                return -1;
            } else if (s1.getGroupNumber() < s2.getGroupNumber() && s1.getGroupNumber() == 0) {
                return 1;
            } else if (s1.getGroupNumber() > s2.getGroupNumber()) {
                return 1;
            } else if (s1.getGroupNumber() == s2.getGroupNumber()) {
                return 1;
            } else return -1;
        }
    }
}
