package main;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import dbclass.Criteria;
import dbclass.Mark;
import dbclass.ProjectInfo;
import dbclass.StudentInfo;
import util.ExcelParser;
import util.ReadExcel;

public class AllFunctions {

    private static AllFunctions allFunctions;
    //initiate the new object: AllFunctions all = AllFunctions.getObject();

    private CommunicationForClient communication;
    private ArrayList<ProjectInfo> projectList = new ArrayList<ProjectInfo>();
    private Handler handlerAllfunction;
    private String username;//for welcome message. this is the firstName.
    private String myEmail;
    private ArrayList<Mark> markListForMarkPage;


    private AllFunctions() {
        communication = new CommunicationForClient(this);
    }

    public void setHandler(Handler hander) {
        handlerAllfunction = hander;
    }

    public void login(final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.login(username, password);
            }
        }).start();
    }

    public void submitRecorder() {
        communication.submitFile();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return this.username;
    }

    public void setMyEmail(String email) {
        this.myEmail = email;
    }

    public String getMyEmail() {
        return this.myEmail;
    }

    public void setMarkListForMarkPage(ArrayList<Mark> markList) {
        this.markListForMarkPage = markList;
        String json = new Gson().toJson(markList);
        Log.d("EEEE", "marklist: " + json);
        handlerAllfunction.sendEmptyMessage(301);
    }

    public ArrayList<Mark> getMarkListForMarkPage() {
        return this.markListForMarkPage;
    }

    public void loginSucc(ArrayList<ProjectInfo> projectList) {
        this.projectList = projectList;
        if (projectList.size() > 0)
            sortStudent();
        handlerAllfunction.sendEmptyMessage(101);
    }

    public void syncSucc(ArrayList<ProjectInfo> projectList) {
        this.projectList = projectList;
        if (projectList.size() > 0)
            sortStudent();
        Log.d("EEEE", this.projectList.toString() + "sync success");
        handlerAllfunction.sendEmptyMessage(210);
    }

    public void syncFail() {
        handlerAllfunction.sendEmptyMessage(211);
    }

    public ArrayList<ProjectInfo> getProjectList() {
        return projectList;
    }

    public void loginFail() {
        handlerAllfunction.sendEmptyMessage(100);
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
                communication.register(firstName, middleName, lastName,
                        email, password);
                Log.d("register", "success");
            }
        }).start();
    }

    public void registerACK(boolean ack) {
        if (ack) {
            handlerAllfunction.sendEmptyMessage(111);
        } else {
            handlerAllfunction.sendEmptyMessage(110);
        }
    }

    public void createProject(String projectName, String subjectName,
                              String subjectCode, String description) {

        ProjectInfo project = new ProjectInfo();
        projectList.add(project);
        project.setUsername(myEmail);
        project.setProjectName(projectName);
        project.setSubjectName(subjectName);
        project.setSubjectCode(subjectCode);
        project.setDescription(description);
        project.getAssistant().add(myEmail);

        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.updateProject_About(projectName, subjectName,
                        subjectCode, description);
                Log.d("createProject", "create new project success");
            }
        }).start();
    }

    public void updateProject(ProjectInfo project, String projectName, String subjectName,
                              String subjectCode, String description) {

        project.setProjectName(projectName);
        project.setSubjectName(subjectName);
        project.setSubjectCode(subjectCode);
        project.setDescription(description);

        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.updateProject_About(projectName, subjectName,
                        subjectCode, description);
                Log.d("createProject", "update old project success");
            }
        }).start();

    }

    public void setAboutACK(String ack) {
        Log.d("EEEE", "set about ack");
        if (ack.equals("true")) {
            Log.d("EEEE", "set about ack true");
            handlerAllfunction.sendEmptyMessage(201);
        } else {
            Log.d("EEEE", "set about ack false");
            handlerAllfunction.sendEmptyMessage(202);
        }
    }

    public void getMarks(ProjectInfo project, int groupNum, String studentID) {
        System.out.println("getMark");
        ArrayList<String> studentIDList = new ArrayList<String>();
        if (groupNum == -999)
            studentIDList.add(studentID);
        else {
            for (int i = 0; i < project.getStudentInfo().size(); i++) {
                if (project.getStudentInfo().get(i).getGroup() == groupNum)
                    studentIDList.add(project.getStudentInfo().get(i).getNumber());
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.getMarks(project, studentIDList);
            }
        }).start();
    }

    public void deleteProject(int index) {
        String projectName = projectList.get(index).getProjectName();
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.deleteProject(projectName);
                Log.d("deleteProject", "success");
            }
        }).start();
        projectList.remove(index);
    }

    public void deleteACK(String ack) {
        if (ack.equals("true")) {
            Log.d("EEEE", "delete ack!!!");
            handlerAllfunction.sendEmptyMessage(205);
        } else {
            handlerAllfunction.sendEmptyMessage(206);
        }
    }

    public void syncProjectList() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.syncProjectList(myEmail);
                Log.d("syncProjectList", "success");
            }
        }).start();
    }

    public void projectTimer(ProjectInfo project, int durationMin, int durationSec,
                             int warningMin, int warningSec) {

        project.setDurationMin(durationMin);
        project.setDurationSec(durationSec);
        project.setWarningMin(warningMin);
        project.setWarningSec(warningSec);

        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.updateProject_Time(project.getProjectName(), durationMin,
                        durationSec, warningMin, warningSec);
                Log.d("projectTimer", "success");
            }
        }).start();
    }

    public void setTimeACK(String ack) {
        if (ack.equals("true")) {
            handlerAllfunction.sendEmptyMessage(203);
        } else {
            handlerAllfunction.sendEmptyMessage(204);
        }
    }

    public void inviteAssessor(ProjectInfo project, String assessorEmail) {
        String projectName = project.getProjectName();
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.inviteAssessor(projectName, assessorEmail);
            }
        }).start();
    }

    public void inviteAssessor_Success(String projectName, String assessorEmail) {
        for (ProjectInfo projectInfo : projectList) {
            if (projectInfo.getProjectName().equals(projectName)) {
                projectInfo.getAssistant().add(assessorEmail);
                handlerAllfunction.sendEmptyMessage(207);
                break;
            }
        }
    }

    public void inviteAssessor_Fail() {
        handlerAllfunction.sendEmptyMessage(208);
    }

    public void deleteAssessor(ProjectInfo project, String assessorEmail) {
        String projectName = project.getProjectName();
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.deleteAssessor(projectName, assessorEmail);
            }
        }).start();
    }

    public void deleteAssessorACK(String ack) {
        if (ack.equals("true")) {
            handlerAllfunction.sendEmptyMessage(309);
        } else {
            handlerAllfunction.sendEmptyMessage(310);
        }
    }


    public void projectCriteria(ProjectInfo project, ArrayList<Criteria> criteriaList,
                                ArrayList<Criteria> commentList) {
        System.out.println("在Allfunction中发送的criteriaList和commentList的数量分别为：" + criteriaList.size() + "  " + commentList.size());

        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.criteriaListSend(project.getProjectName(), criteriaList, commentList);
                Log.d("readExcel", "success");
            }
        }).start();
    }

    public void updateProjectCriteriaACK(String ack) {
        if (ack.equals("true")) {
            handlerAllfunction.sendEmptyMessage(401);
        } else {
            handlerAllfunction.sendEmptyMessage(402);
        }
    }

    public void addStudentsFromExcel(ProjectInfo project, ArrayList<StudentInfo> students) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.importStudents(project.getProjectName(), students);
            }
        }).start();
    }

    public void uploadStudentsACK(String ack) {
        if (ack.equals("true")) {
            Log.d("EEEE", "successfully upload students");
            handlerAllfunction.sendEmptyMessage(225);
        } else {
            Log.d("EEEE", "fail to upload students");
            handlerAllfunction.sendEmptyMessage(226);
        }
    }

    public ArrayList<Criteria> readCriteriaExcel(ProjectInfo project, String path) {
        ExcelParser excelParser = new ExcelParser();
        Log.d("EEEE", "path: " + path);
        if (path.endsWith(".xls")) {
            Log.d("EEEE", "read xls file.");
            return excelParser.readXlsCriteria(path);
        } else if (path.endsWith(".xlsx")) {
            Log.d("EEEE", "read xlsx file.");
            return excelParser.readXlsxCriteria(path);
        }
        return null;
    }

    public void addStudent(ProjectInfo project, String number, String firstName,
                           String middleName, String surname, String email) {
        StudentInfo studentInfo = new StudentInfo(number, firstName, middleName, surname, email);
        project.addSingleStudent(studentInfo);

        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.addStudent(project.getProjectName(), number,
                        firstName, middleName, surname, email);
                Log.d("addStudent", "success");
            }
        }).start();
    }

    public void addStudentACK(String ack) {
        if (ack.equals("true")) {
            Log.d("EEEE", "add student successfully");
            handlerAllfunction.sendEmptyMessage(221);
        } else {
            Log.d("EEEE", "fail to add student");
            handlerAllfunction.sendEmptyMessage(222);
        }
    }

    public int searchStudent(ProjectInfo project, String number) {
        ArrayList<StudentInfo> list = project.getStudentInfo();

        //test
        System.out.println("list size in search student: " + list.size());
        for (int i = 0; i < list.size(); i++) {
            //test
            // System.out.println("The "+i+" student number: "+list.get(i).getNumber());
            if (number.equals(list.get(i).getNumber())) {
                return i;
            }
        }
        return -999;
    }

    public void editStudent(ProjectInfo project, String number, String firstName,
                            String middleName, String surname, String email) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.editStudent(project.getProjectName(), number, firstName,
                        middleName, surname, email);
                Log.d("editStudent", "success");
            }
        }).start();
    }

    public void editStudentACK(String ack) {
        if (ack.equals("true")) {
            Log.d("EEEE", "edit student successfully");
            handlerAllfunction.sendEmptyMessage(223);
        } else {
            Log.d("EEEE", "fail to edit student");
            handlerAllfunction.sendEmptyMessage(224);
        }
    }

    public void deleteStudent(ProjectInfo project, String number) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.deleteStudent(project.getProjectName(), number);
                Log.d("deleteStudent", "success");
            }
        }).start();
    }

    public void groupStudent(ProjectInfo project, String studentID, int groupNumber) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.groupStudent(project.getProjectName(), studentID, groupNumber);
                Log.d("groupStudent", "success");
            }
        }).start();

    }

    public int getMaxGroupNumber(int indexOfProject) {
        int max = 0;
        for (StudentInfo student : projectList.get(indexOfProject).getStudentInfo()) {
            if (student.getGroup() > max)
                max = student.getGroup();
        }
        return max;
    }

    public void sendMark(ProjectInfo project, String studentID, Mark mark) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.sendMark(project, studentID, mark);
                Log.d("sendMark", "success");
            }
        }).start();
    }

    public void sendMarkACK(String ack) {
        if (ack.equals("true")) {
            Log.d("EEEE", "send mark successfully");
            handlerAllfunction.sendEmptyMessage(351);
        } else {
            Log.d("EEEE", "fail to send mark");
            handlerAllfunction.sendEmptyMessage(352);
        }
    }

    public void sendPDF(ProjectInfo project, String studentID, int sendBoth) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.sendPDF(project.getProjectName(),
                        studentID, sendBoth);
                Log.d("sendMark", "success");
            }
        }).start();
    }


    public void sortStudent() {
        for (int i = 0; i < projectList.size(); i++) {
            Collections.sort(projectList.get(i).getStudentInfo(), new SortByGroup());
        }
    }

    public class SortByGroup implements Comparator {
        public int compare(Object o1, Object o2) {
            StudentInfo s1 = (StudentInfo) o1;
            StudentInfo s2 = (StudentInfo) o2;
            if (s1.getGroup() > s2.getGroup() && s2.getGroup() == -999) {
                return -1;
            } else if (s1.getGroup() < s2.getGroup() && s1.getGroup() == -999) {
                return 1;
            } else if (s1.getGroup() > s2.getGroup()) {
                return 1;
            } else if (s1.getGroup() == s2.getGroup()) {
                return 1;
            } else return -1;
        }
    }


    public void testSortGroup() {

        ArrayList<StudentInfo> studentListForTest = new ArrayList<>();
        StudentInfo student1 = new StudentInfo();
        student1.setGroup(-999);
        studentListForTest.add(student1);
        StudentInfo student2 = new StudentInfo();
        student2.setGroup(2);
        studentListForTest.add(student2);
        StudentInfo student3 = new StudentInfo();
        student3.setGroup(-999);
        studentListForTest.add(student3);
        StudentInfo student4 = new StudentInfo();
        student4.setGroup(-999);
        studentListForTest.add(student4);
        StudentInfo student5 = new StudentInfo();
        student5.setGroup(1);
        studentListForTest.add(student5);
        StudentInfo student6 = new StudentInfo();
        student6.setGroup(2);
        studentListForTest.add(student6);
        StudentInfo student7 = new StudentInfo();
        student7.setGroup(77);
        studentListForTest.add(student7);

        Collections.sort(studentListForTest, new SortByGroup());

        System.out.println("sort starts：");
        for (StudentInfo s : studentListForTest)
            System.out.println(s.getGroup());
        System.out.println("sort completed");
    }

}
