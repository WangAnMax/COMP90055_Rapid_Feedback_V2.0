package main;

import android.util.Log;
import com.alibaba.fastjson.JSONObject;
import com.example.feedback.Activity_Login;

import java.util.ArrayList;
import java.util.List;

import newdbclass.Criterion;
import newdbclass.Project;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import util.JSONUtil;


public class CommunicationForClient {
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private String host;
    private OkHttpClient client;
    private String token;
    //private String myUsername;
    AllFunctions functions;
    public static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("audio/mpeg");

    public CommunicationForClient(AllFunctions functions) {
        host = "http://10.13.88.39:8080/RapidFeedback/";
//        host = "http://192.168.0.13:8080/RapidFeedback/";
//        host = "http://10.13.216.108:8080/RapidFeedback";
        client = new OkHttpClient();
        this.functions = functions;
    }

    public void register(String firstName, String middleName, String lastName,
                         String email, String password) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("firstName", firstName);
        jsonSend.put("middleName", middleName);
        jsonSend.put("lastName", lastName);
        jsonSend.put("email", email);
        jsonSend.put("password", password);
        Log.d("EEEE", "Send: " + jsonSend.toJSONString()); //just for test

        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/RegisterServlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            Log.d("EEEE", "Receive: " + receive); //just for test
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            int register_ACK = Integer.parseInt(jsonReceive.get("id").toString());
            functions.registerACK(register_ACK);
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void login(String username, String password) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("username", username);
        jsonSend.put("password", password);
        Log.d("EEEE", "Send: " + jsonSend.toJSONString()); //just for test

        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/LoginServlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            JSONUtil.write(receive);
            Log.d("EEEE", "Receive: " + receive); //just for test
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            Activity_Login.mUserInfoOpertor.saveUserInfo(username, password);
            //get projectlist from jsonReceive
            String projectListString = jsonReceive.get("projectList").toString();
            JSONUtil.write(projectListString);
            String firstName = jsonReceive.getString("firstName");
            List<Project> projectList = JSONObject.parseArray(projectListString, Project.class);
            ArrayList<Project> arrayList = new ArrayList();
            arrayList.addAll(projectList);
            functions.setUsername(firstName);
            functions.setUserEmail(username);
            Log.d("EEEE", "when login firstName received is: " + firstName);
            token = jsonReceive.getString("token");
            int login_ACK = Integer.parseInt(jsonReceive.get("login_ACK").toString());
            functions.setId(login_ACK);
            functions.loginACK(login_ACK);
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void syncProjectList(int id) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("userId", id);
        jsonSend.put("token", token);
        Log.d("EEEE", "Send: " + jsonSend.toJSONString()); //just for test

        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/SyncProjectListServlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            Log.d("EEEE", "Receive: " + receive); //just for test
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            //get projectlist from jsonReceive
            String projectListString = jsonReceive.get("projectList").toString();
            List<Project> projectList = JSONObject.parseArray(projectListString, Project.class);
            ArrayList<Project> arrayList = new ArrayList();
            arrayList.addAll(projectList);
            Boolean sync_ACK = Boolean.parseBoolean(jsonReceive.get("syn_ACK").toString());
            functions.syncACK(sync_ACK, arrayList);
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void updateProject_About(String projectName, String subjectName,
                                    String subjectCode, String description,
                                    int durationSec, int warningSec, int userId) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectName", projectName);
        jsonSend.put("subjectName", subjectName);
        jsonSend.put("subjectCode", subjectCode);
        jsonSend.put("description", description);
        jsonSend.put("durationSec", durationSec);
        jsonSend.put("warningSec", warningSec);
        jsonSend.put("principalId", userId);
        Log.d("EEEE", "Send: " + jsonSend.toJSONString()); //just for test

        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/UpdateProject_About_Servlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            Log.d("EEEE", "Receive: " + receive); //just for test
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean updateProject_ACK = Boolean.parseBoolean(jsonReceive.get("updateProject_ACK").toString());
            int projectId = Integer.parseInt(jsonReceive.get("projectId").toString());
            functions.setAboutACK(updateProject_ACK, projectId);
        } catch (Exception e1) {
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void deleteProject(int projectId) {
        //construct JSONObject to send
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        Log.d("EEEE", "Send: " + jsonSend.toJSONString()); //just for test
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/DeleteProjectServlet")
                .post(body)
                .build();

        //get the JSONObject from response
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            Log.d("EEEE", "Receive: " + receive); //just for test
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean updateStudent_ACK = Boolean.parseBoolean(jsonReceive.get("updateProject_ACK").toString());
            functions.deleteACK(updateStudent_ACK);
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }

    public void criteriaListSend(ArrayList<Criterion> markedCriteriaList, int projectId) {
        JSONObject jsonSend = new JSONObject();
        jsonSend.put("token", token);
        jsonSend.put("projectId", projectId);
        String markedCriteriaListString = com.alibaba.fastjson.JSON.toJSONString(markedCriteriaList);
        jsonSend.put("criteriaList", markedCriteriaListString);
        Log.d("EEEE", "Send: " + jsonSend.toJSONString()); //just for test
        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
        Request request = new Request.Builder()
                .url(host + "/CriteriaListServlet")
                .post(body)
                .build();
        try (Response response = client.newCall(request).execute()) {
            String receive = response.body().string();
            System.out.println("Receive: " + receive); //just for test
            JSONObject jsonReceive = JSONObject.parseObject(receive);
            boolean updateProject_ACK = Boolean.parseBoolean(jsonReceive.get("updateProject_ACK").toString());
            functions.updateProjectCriteriaACK(updateProject_ACK);
        } catch (Exception e1) {
            e1.printStackTrace();
            AllFunctions.getObject().exceptionWithServer();
        }
    }


//    public void updateProject_Time(String projectName, int durationMin, int durationSec,
//                                   int warningMin, int warningSec) {
//        JSONObject jsonSend = new JSONObject();
//        jsonSend.put("token", token);
//        jsonSend.put("projectName", projectName);
//        jsonSend.put("durationMin", durationMin);
//        jsonSend.put("durationSec", durationSec);
//        jsonSend.put("warningMin", warningMin);
//        jsonSend.put("warningSec", warningSec);
//        System.out.println("Send: " + jsonSend.toJSONString()); //just for test
//
//        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
//        Request request = new Request.Builder()
//                .url(host + "UpdateProject_Time_Servlet")
//                .post(body)
//                .build();
//        try (Response response = client.newCall(request).execute()) {
//            String receive = response.body().string();
//            System.out.println("Receive: " + receive); //just for test
//            JSONObject jsonReceive = JSONObject.parseObject(receive);
//            String updateProject_ACK = jsonReceive.get("updateProject_ACK").toString();
//            functions.setTimeACK(updateProject_ACK);
//            if (updateProject_ACK.equals("true")) {
//                ;
//            } else {
//                //失败跳出
//            }
//        } catch (Exception e1) {
//            AllFunctions.getObject().exceptionWithServer();
//        }
//    }
//
//    public void addStudent(String projectName, String studentID, String firstName,
//                           String middleName, String lastName, String email) {
//        //construct JSONObject to send
//        JSONObject jsonSend = new JSONObject();
//        jsonSend.put("token", token);
//        jsonSend.put("projectName", projectName);
//        jsonSend.put("studentID", studentID);
//        jsonSend.put("firstName", firstName);
//        jsonSend.put("middleName", middleName);
//        jsonSend.put("lastName", lastName);
//        jsonSend.put("email", email);
//
//        System.out.println("Send: " + jsonSend.toJSONString()); //just for test
//
//        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
//        Request request = new Request.Builder()
//                .url(host + "AddStudentServlet")
//                .post(body)
//                .build();
//
//        //get the JSONObject from response
//        try (Response response = client.newCall(request).execute()) {
//            String receive = response.body().string();
//            System.out.println("Receive: " + receive); //just for test
//            JSONObject jsonReceive = JSONObject.parseObject(receive);
//            String updateStudent_ACK = jsonReceive.get("updateStudent_ACK").toString();
//            functions.addStudentACK(updateStudent_ACK);
//            if (updateStudent_ACK.equals("true")) {
//                ;
//            } else {
//                //失败跳出
//            }
//        } catch (Exception e1) {
//            AllFunctions.getObject().exceptionWithServer();
//        }
//    }
//
//    public void editStudent(String projectName, String studentID, String firstName,
//                            String middleName, String lastName, String email) {
//        //construct JSONObject to send
//        JSONObject jsonSend = new JSONObject();
//        jsonSend.put("token", token);
//        jsonSend.put("projectName", projectName);
//        jsonSend.put("studentID", studentID);
//        jsonSend.put("firstName", firstName);
//        jsonSend.put("middleName", middleName);
//        jsonSend.put("lastName", lastName);
//        jsonSend.put("email", email);
//
//        System.out.println("Send: " + jsonSend.toJSONString()); //just for test
//
//        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
//        Request request = new Request.Builder()
//                .url(host + "EditStudentServlet")
//                .post(body)
//                .build();
//
//        //get the JSONObject from response
//        try (Response response = client.newCall(request).execute()) {
//            String receive = response.body().string();
//
//            System.out.println("Receive: " + receive); //just for test
//
//            JSONObject jsonReceive = JSONObject.parseObject(receive);
//            String updateStudent_ACK = jsonReceive.get("updateStudent_ACK").toString();
//            functions.editStudentACK(updateStudent_ACK);
//            if (updateStudent_ACK.equals("true")) {
//                ;
//            } else {
//                //失败跳出
//            }
//        } catch (Exception e1) {
//            AllFunctions.getObject().exceptionWithServer();
//        }
//    }
//
//
//    public void groupStudent(String projectName, String studentID, int groupNumber) {
//        //construct JSONObject to send
//        JSONObject jsonSend = new JSONObject();
//        jsonSend.put("token", token);
//        jsonSend.put("projectName", projectName);
//        jsonSend.put("studentID", studentID);
//        jsonSend.put("group", groupNumber);
//
//        System.out.println("Send in group student method: " + jsonSend.toJSONString()); //just for test
//
//        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
//        Request request = new Request.Builder()
//                .url(host + "GroupStudentServlet")
//                .post(body)
//                .build();
//
//        //get the JSONObject from response
//        try (Response response = client.newCall(request).execute()) {
//            String receive = response.body().string();
//
//            System.out.println("Receive: " + receive); //just for test
//
//            JSONObject jsonReceive = JSONObject.parseObject(receive);
//            String updateStudent_ACK = jsonReceive.get("updateStudent_ACK").toString();
//            if (updateStudent_ACK.equals("true")) {
//                ;
//            } else {
//                AllFunctions.getObject().exceptionWithServer();
//            }
//        } catch (Exception e1) {
//            AllFunctions.getObject().exceptionWithServer();
//        }
//    }
//
//
//    public void deleteStudent(String projectName, String studentID) {
//        //construct JSONObject to send
//        JSONObject jsonSend = new JSONObject();
//        jsonSend.put("token", token);
//        jsonSend.put("projectName", projectName);
//        jsonSend.put("studentID", studentID);
//
//        System.out.println("Send: " + jsonSend.toJSONString()); //just for test
//
//        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
//        Request request = new Request.Builder()
//                .url(host + "DeleteStudentServlet")
//                .post(body)
//                .build();
//
//        //get the JSONObject from response
//        try (Response response = client.newCall(request).execute()) {
//            String receive = response.body().string();
//
//            System.out.println("Receive: " + receive); //just for test
//
//            JSONObject jsonReceive = JSONObject.parseObject(receive);
//            String updateStudent_ACK = jsonReceive.get("updateStudent_ACK").toString();
//            if (updateStudent_ACK.equals("true")) {
//                ;
//            } else {
//                //失败跳出
//            }
//        } catch (Exception e1) {
//            AllFunctions.getObject().exceptionWithServer();
//        }
//    }
//
//
//    public void inviteAssessor(String projectName, String assessorEmail) {
//        //construct JSONObject to send
//        JSONObject jsonSend = new JSONObject();
//        jsonSend.put("token", token);
//        jsonSend.put("projectName", projectName);
//        jsonSend.put("assessorEmail", assessorEmail);
//
//        System.out.println("Send: " + jsonSend.toJSONString()); //just for test
//
//        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
//        Request request = new Request.Builder()
//                .url(host + "InviteAssessorServlet")
//                .post(body)
//                .build();
//
//        //get the JSONObject from response
//        try (Response response = client.newCall(request).execute()) {
//            String receive = response.body().string();
//
//            System.out.println("Receive: " + receive); //just for test
//
//            JSONObject jsonReceive = JSONObject.parseObject(receive);
//            String invite_ACK = jsonReceive.get("invite_ACK").toString();
//            if (invite_ACK.equals("true")) {
//                AllFunctions.getObject().inviteAssessor_Success(projectName, assessorEmail);
//            } else {
//                AllFunctions.getObject().inviteAssessor_Fail();
//            }
//        } catch (Exception e1) {
//            AllFunctions.getObject().exceptionWithServer();
//        }
//    }
//
//    public void deleteAssessor(String projectName, String assessorEmail) {
//        //construct JSONObject to send
//        JSONObject jsonSend = new JSONObject();
//        jsonSend.put("token", token);
//        jsonSend.put("projectName", projectName);
//        jsonSend.put("assessorEmail", assessorEmail);
//
//        System.out.println("Send: " + jsonSend.toJSONString()); //just for test
//
//        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
//        Request request = new Request.Builder()
//                .url(host + "InviteAssessorServlet")
//                .delete(body)
//                .build();
//
//        //get the JSONObject from response
//        try (Response response = client.newCall(request).execute()) {
//            String receive = response.body().string();
//
//            System.out.println("Receive: " + receive); //just for test
//
//            JSONObject jsonReceive = JSONObject.parseObject(receive);
//            String delete_ACK = jsonReceive.get("delete_ACK").toString();
//            functions.deleteAssessorACK(delete_ACK);
//            if (delete_ACK.equals("true")) {
//                ;
//            } else {
//                //失败跳出
//            }
//        } catch (Exception e1) {
//            AllFunctions.getObject().exceptionWithServer();
//        }
//    }
//
//
////    public void getMarks(ProjectInfo project, ArrayList<String> studentIDList) {
////        System.out.println("Communication的getMark方法被调用");
////        //construct JSONObject to send
////        JSONObject jsonSend = new JSONObject();
////        jsonSend.put("token", token);
////        jsonSend.put("projectName", project.getProjectName());
////        String studentIDListString = com.alibaba.fastjson.JSON.toJSONString(studentIDList);
////        jsonSend.put("studentNumberList", studentIDListString);
////        jsonSend.put("primaryEmail", project.getAssistant().get(0));
////
////
////        System.out.println("Send: " + jsonSend.toJSONString()); //just for test
////
////        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
////        Request request = new Request.Builder()
////                .url(host + "GetMarkServlet")
////                .post(body)
////                .build();
////
////        //get the JSONObject from response
////        try (Response response = client.newCall(request).execute()) {
////            String receive = response.body().string();
////
////            System.out.println("Receive: " + receive); //just for test
////
////            JSONObject jsonReceive = JSONObject.parseObject(receive);
////            String mark_ACK = jsonReceive.get("getMark_ACK").toString();
////            if (mark_ACK.equals("true")) {
////                String markListString = jsonReceive.get("markList").toString();
////                List<Mark> markList = JSONObject.parseArray(markListString, Mark.class);
////                ArrayList<Mark> arrayList = new ArrayList();
////                arrayList.addAll(markList);
////                Log.d("EEEE", "get marks: " + arrayList.toString());
////                AllFunctions.getObject().setMarkListForMarkPage(arrayList);
////            } else {
////                //失败跳出
////            }
////        } catch (Exception e1) {
////            AllFunctions.getObject().exceptionWithServer();
////        }
////    }
////
////
////    public void sendMark(ProjectInfo project, String studentID, Mark mark) {
////        //construct JSONObject to send
////        JSONObject jsonSend = new JSONObject();
////        jsonSend.put("token", token);
////        jsonSend.put("projectName", project.getProjectName());
////        jsonSend.put("studentID", studentID);
////        String markString = com.alibaba.fastjson.JSON.toJSONString(mark);
////        jsonSend.put("mark", markString);
////        jsonSend.put("primaryEmail", project.getAssistant().get(0));
////
////        System.out.println("Send in method sendMark: " + jsonSend.toJSONString()); //just for test
////
////        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
////        Request request = new Request.Builder()
////                .url(host + "MarkServlet")
////                .post(body)
////                .build();
////
////        //get the JSONObject from response
////        try (Response response = client.newCall(request).execute()) {
////            String receive = response.body().string();
////
////            System.out.println("Receive: " + receive); //just for test
////
////            JSONObject jsonReceive = JSONObject.parseObject(receive);
////            String mark_ACK = jsonReceive.get("mark_ACK").toString();
////            functions.sendMarkACK(mark_ACK);
////            if (mark_ACK.equals("true")) {
////                Log.d("EEEE", "send mark success");
////            } else {
////                //失败跳出
////            }
////        } catch (Exception e1) {
////            AllFunctions.getObject().exceptionWithServer();
////        }
////    }
////
////
////    public void sendPDF(String projectName, String studentID, int sendBoth) {
////        //construct JSONObject to send
////        JSONObject jsonSend = new JSONObject();
////        jsonSend.put("token", token);
////        jsonSend.put("projectName", projectName);
////        jsonSend.put("studentID", studentID);
////        jsonSend.put("sendBoth", sendBoth);
////
////        System.out.println("Send in method sendPDF: " + jsonSend.toJSONString()); //just for test
////
////        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
////        Request request = new Request.Builder()
////                .url(host + "SendEmailServlet")
////                .post(body)
////                .build();
////
////        //get the JSONObject from response
////        try (Response response = client.newCall(request).execute()) {
////            String receive = response.body().string();
////
////            System.out.println("Receive: " + receive); //just for test
////
////            JSONObject jsonReceive = JSONObject.parseObject(receive);
////            String sendMail_ACK = jsonReceive.get("sendMail_ACK").toString();
////            if (sendMail_ACK.equals("true")) {
////                ;
////            } else {
////                //失败跳出
////            }
////        } catch (Exception e1) {
////            AllFunctions.getObject().exceptionWithServer();
////        }
////    }
////
////
////    public void importStudents(String projectName, ArrayList<StudentInfo> studentList) {
////        //construct JSONObject to send
////        JSONObject jsonSend = new JSONObject();
////        jsonSend.put("token", token);
////        jsonSend.put("projectName", projectName);
////        String studentListString = com.alibaba.fastjson.JSON.toJSONString(studentList);
////        jsonSend.put("studentList", studentListString);
////
////        System.out.println("Send: " + jsonSend.toJSONString()); //just for test
////
////        RequestBody body = RequestBody.create(JSON, jsonSend.toJSONString());
////        Request request = new Request.Builder()
////                .url(host + "ImportStudentsServlet")
////                .post(body)
////                .build();
////
////        //get the JSONObject from response
////        try (Response response = client.newCall(request).execute()) {
////            String receive = response.body().string();
////
////            Log.d("EEEE", "Receive: " + receive); //just for test
////
////            JSONObject jsonReceive = JSONObject.parseObject(receive);
////            String updateStudent_ACK = jsonReceive.get("updateStudent_ACK").toString();
////            functions.uploadStudentsACK(updateStudent_ACK);
////            if (updateStudent_ACK.equals("true")) {
////
////            } else {
////                //失败跳出
////            }
////        } catch (Exception e1) {
////            AllFunctions.getObject().exceptionWithServer();
////        }
////    }
////
////    public void submitFile(){
////        //test a existed file
////        File f = new File(Environment.getExternalStorageDirectory()+"/SoundRecorder"+"/My Recording_7.mp4");
////
////        RequestBody body = RequestBody.create(MEDIA_TYPE_MARKDOWN, f);
////        MultipartBody multipartBody = new MultipartBody.Builder()
////                // set type as "multipart/form-data"，otherwise cannot upload file
////                .setType(MultipartBody.FORM)
////                .addFormDataPart("filename", "recorder", body)
////                .build();
////        //for test
////        Log.d("submit", "in");
////
////        Request request = new Request.Builder()
////                .url(host + "AudioRecorderServlet")
////                .post(multipartBody)
////                .build();
////
////        //callback
////        client.newCall(request).enqueue(new Callback() {
////            @Override
////            public void onFailure(Call call, IOException e) {
////            }
////            @Override
////            public void onResponse(Call call, Response response) throws IOException {
////                System.out.println("get back Parameter：\n" + response.body().string());
////            }
////        });
////    }
}