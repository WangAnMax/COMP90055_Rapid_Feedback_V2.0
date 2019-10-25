package main;

import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;

import newdbclass.Criterion;
import newdbclass.Project;

public class AllFunctions {

    private static AllFunctions allFunctions;
    //initiate the new object: AllFunctions all = AllFunctions.getObject();

    private CommunicationForClient communication;
    private ArrayList<Project> projectList = new ArrayList<Project>();
    private Handler handlerAllfunction;
    private String username;//for welcome message. this is the firstName.
    private String userEmail;
    private int userId;
    private int projectId;

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
                Log.d("syncProjectList", "success");
            }
        }).start();
    }

    public void syncACK(boolean ack, ArrayList<Project> projectList) {
        if (ack) { // sync success
            this.projectList = projectList;
            handlerAllfunction.sendEmptyMessage(108);
        } else { // succ fail
            handlerAllfunction.sendEmptyMessage(109);
        }
    }

    public void updateProject(String projectName, String subjectName,
                              String subjectCode, String description,
                              int durationSec, int warningSec) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.updateProject_About(projectName, subjectName,
                        subjectCode, description, durationSec, warningSec, userId);
                Log.d("createProject", "create new project success");
            }
        }).start();
    }

    public void setAboutACK(boolean ack, int projectId) {
        if (ack) {
            Log.d("EEEE", "set about ack true");
            this.projectId = projectId;
            handlerAllfunction.sendEmptyMessage(110);
        } else {
            Log.d("EEEE", "set about ack false");
            handlerAllfunction.sendEmptyMessage(111);
        }
    }

    public void deleteProject(int index) {
        int projectId = this.projectId;
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.deleteProject(projectId);
                Log.d("deleteProject", "success");
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

    public void updateProjectCriteria(ArrayList<Criterion> criteriaList) {
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

    public void inviteMarker(int markerId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.updateMarker(markerId, projectId);
            }
        }).start();
    }

    public void deleteMarker(int markerId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                communication.updateMarker(markerId, projectId);
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


//    public void setMarkListForMarkPage(ArrayList<Mark> markList) {
//        this.markListForMarkPage = markList;
//        String json = new Gson().toJson(markList);
//        Log.d("EEEE", "marklist: " + json);
//        handlerAllfunction.sendEmptyMessage(301);
//    }
//
//    public ArrayList<Mark> getMarkListForMarkPage() {
//        return this.markListForMarkPage;
//    }
//
//
//    public void getMarks(ProjectInfo project, int groupNum, String studentID) {
//        System.out.println("getMark");
//        ArrayList<String> studentIDList = new ArrayList<String>();
//        if (groupNum == -999)
//            studentIDList.add(studentID);
//        else {
//            for (int i = 0; i < project.getStudentInfo().size(); i++) {
//                if (project.getStudentInfo().get(i).getGroup() == groupNum)
//                    studentIDList.add(project.getStudentInfo().get(i).getNumber());
//            }
//        }
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                communication.getMarks(project, studentIDList);
//            }
//        }).start();
//    }

//
//    public void projectTimer(ProjectInfo project, int durationMin, int durationSec,
//                             int warningMin, int warningSec) {
//
//        project.setDurationMin(durationMin);
//        project.setDurationSec(durationSec);
//        project.setWarningMin(warningMin);
//        project.setWarningSec(warningSec);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                communication.updateProject_Time(project.getProjectName(), durationMin,
//                        durationSec, warningMin, warningSec);
//                Log.d("projectTimer", "success");
//            }
//        }).start();
//    }
//
//    public void setTimeACK(String ack) {
//        if (ack.equals("true")) {
//            handlerAllfunction.sendEmptyMessage(203);
//        } else {
//            handlerAllfunction.sendEmptyMessage(204);
//        }
//    }
//

//
//    public void inviteAssessor_Success(String projectName, String assessorEmail) {
//        for (ProjectInfo projectInfo : projectList) {
//            if (projectInfo.getProjectName().equals(projectName)) {
//                projectInfo.getAssistant().add(assessorEmail);
//                handlerAllfunction.sendEmptyMessage(207);
//                break;
//            }
//        }
//    }
//
//    public void inviteAssessor_Fail() {
//        handlerAllfunction.sendEmptyMessage(208);
//    }
//
//

//
//


//
//    public void addStudentsFromExcel(ProjectInfo project, ArrayList<StudentInfo> students) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                communication.importStudents(project.getProjectName(), students);
//            }
//        }).start();
//    }
//
//    public void uploadStudentsACK(String ack) {
//        if (ack.equals("true")) {
//            Log.d("EEEE", "successfully upload students");
//            handlerAllfunction.sendEmptyMessage(225);
//        } else {
//            Log.d("EEEE", "fail to upload students");
//            handlerAllfunction.sendEmptyMessage(226);
//        }
//    }
//
//    public ArrayList<Criteria> readCriteriaExcel(ProjectInfo project, String path) {
//        ExcelParser excelParser = new ExcelParser();
//        Log.d("EEEE", "path: " + path);
//        if (path.endsWith(".xls")) {
//            Log.d("EEEE", "read xls file.");
//            return excelParser.readXlsCriteria(path);
//        } else if (path.endsWith(".xlsx")) {
//            Log.d("EEEE", "read xlsx file.");
//            return excelParser.readXlsxCriteria(path);
//        }
//        return null;
//    }
//
//    public void addStudent(ProjectInfo project, String number, String firstName,
//                           String middleName, String surname, String email) {
//        StudentInfo studentInfo = new StudentInfo(number, firstName, middleName, surname, email);
//        project.addSingleStudent(studentInfo);
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                communication.addStudent(project.getProjectName(), number,
//                        firstName, middleName, surname, email);
//                Log.d("addStudent", "success");
//            }
//        }).start();
//    }
//
//    public void addStudentACK(String ack) {
//        if (ack.equals("true")) {
//            Log.d("EEEE", "add student successfully");
//            handlerAllfunction.sendEmptyMessage(221);
//        } else {
//            Log.d("EEEE", "fail to add student");
//            handlerAllfunction.sendEmptyMessage(222);
//        }
//    }
//
//    public int searchStudent(ProjectInfo project, String number) {
//        ArrayList<StudentInfo> list = project.getStudentInfo();
//
//        //test
//        System.out.println("list size in search student: " + list.size());
//        for (int i = 0; i < list.size(); i++) {
//            //test
//            // System.out.println("The "+i+" student number: "+list.get(i).getNumber());
//            if (number.equals(list.get(i).getNumber())) {
//                return i;
//            }
//        }
//        return -999;
//    }
//
//    public void editStudent(ProjectInfo project, String number, String firstName,
//                            String middleName, String surname, String email) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                communication.editStudent(project.getProjectName(), number, firstName,
//                        middleName, surname, email);
//                Log.d("editStudent", "success");
//            }
//        }).start();
//    }
//
//    public void editStudentACK(String ack) {
//        if (ack.equals("true")) {
//            Log.d("EEEE", "edit student successfully");
//            handlerAllfunction.sendEmptyMessage(223);
//        } else {
//            Log.d("EEEE", "fail to edit student");
//            handlerAllfunction.sendEmptyMessage(224);
//        }
//    }
//
//    public void deleteStudent(ProjectInfo project, String number) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                communication.deleteStudent(project.getProjectName(), number);
//                Log.d("deleteStudent", "success");
//            }
//        }).start();
//    }
//
//    public void groupStudent(ProjectInfo project, String studentID, int groupNumber) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                communication.groupStudent(project.getProjectName(), studentID, groupNumber);
//                Log.d("groupStudent", "success");
//            }
//        }).start();
//
//    }
//
//    public int getMaxGroupNumber(int indexOfProject) {
//        int max = 0;
//        for (StudentInfo student : projectList.get(indexOfProject).getStudentInfo()) {
//            if (student.getGroup() > max)
//                max = student.getGroup();
//        }
//        return max;
//    }
//
//    public void sendMark(ProjectInfo project, String studentID, Mark mark) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                communication.sendMark(project, studentID, mark);
//                Log.d("sendMark", "success");
//            }
//        }).start();
//    }
//
//    public void sendMarkACK(String ack) {
//        if (ack.equals("true")) {
//            Log.d("EEEE", "send mark successfully");
//            handlerAllfunction.sendEmptyMessage(351);
//        } else {
//            Log.d("EEEE", "fail to send mark");
//            handlerAllfunction.sendEmptyMessage(352);
//        }
//    }
//
//    public void sendPDF(ProjectInfo project, String studentID, int sendBoth) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                communication.sendPDF(project.getProjectName(),
//                        studentID, sendBoth);
//                Log.d("sendMark", "success");
//            }
//        }).start();
//    }
//
//
//    public void sortStudent() {
//        for (int i = 0; i < projectList.size(); i++) {
//            Collections.sort(projectList.get(i).getStudentList(), new SortByGroup());
//        }
//    }
//
//    public class SortByGroup implements Comparator {
//        public int compare(Object o1, Object o2) {
//            Student s1 = (Student) o1;
//            Student s2 = (Student) o2;
//            if (s1.getGroup() > s2.getGroup() && s2.getGroup() == -999) {
//                return -1;
//            } else if (s1.getGroup() < s2.getGroup() && s1.getGroup() == -999) {
//                return 1;
//            } else if (s1.getGroup() > s2.getGroup()) {
//                return 1;
//            } else if (s1.getGroup() == s2.getGroup()) {
//                return 1;
//            } else return -1;
//        }
//    }


//    public void testSortGroup() {
//
//        ArrayList<StudentInfo> studentListForTest = new ArrayList<>();
//        StudentInfo student1 = new StudentInfo();
//        student1.setGroup(-999);
//        studentListForTest.add(student1);
//        StudentInfo student2 = new StudentInfo();
//        student2.setGroup(2);
//        studentListForTest.add(student2);
//        StudentInfo student3 = new StudentInfo();
//        student3.setGroup(-999);
//        studentListForTest.add(student3);
//        StudentInfo student4 = new StudentInfo();
//        student4.setGroup(-999);
//        studentListForTest.add(student4);
//        StudentInfo student5 = new StudentInfo();
//        student5.setGroup(1);
//        studentListForTest.add(student5);
//        StudentInfo student6 = new StudentInfo();
//        student6.setGroup(2);
//        studentListForTest.add(student6);
//        StudentInfo student7 = new StudentInfo();
//        student7.setGroup(77);
//        studentListForTest.add(student7);
//
//        Collections.sort(studentListForTest, new SortByGroup());
//
//        System.out.println("sort starts：");
//        for (StudentInfo s : studentListForTest)
//            System.out.println(s.getGroup());
//        System.out.println("sort completed");
//    }
}
